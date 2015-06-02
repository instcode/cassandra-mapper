package util
/**
The Play (2.3) json combinator library is arguably the best in the scala world. However it doesnt
      work with case classes with greater than 22 fields.
      The following gist leverages the shapeless 'Automatic Typeclass Derivation' facility to work around this
      limitation. Simply stick it in a common location in your code base, and use like so:
      Note: ** Requires Play 2.3 and shapeless 2.1.0  
      
  
        import SWrites._
        import SReads._
    
        case class Foo(value: String)  
        case class Bar(value1: Int, foo: Foo) //Didnt want to type out 23 fields, but you get the idea
        
        
          implicit val writes: Writes[Foo] = SWrites.deriveInstance
          implicit val reads: Reads[Foo] = SReads.deriveInstance
          
          implicit val writes: Writes[Bar] = SWrites.deriveInstance
          implicit val reads: Reads[Bar] = SReads.deriveInstance



      Additionally, you may get boilerplate free Format typeclasses:


      import SFormats._

      case class Foo(value: String)  
      case class Bar(value1: Int, foo: Foo)

      def someFunc(value: T)(implicit val format: Format[T]) = ... 
  **/

import play.api.libs._
import json._

import shapeless.{ `::` => :#:, _ }
import poly._

object SWrites extends LabelledTypeClassCompanion[Writes] {
  object typeClass extends LabelledTypeClass[Writes] {

    def emptyProduct: Writes[HNil] = Writes(_ => JsNull)

    def product[F, T <: HList](name: String, FHead: Writes[F], FTail: Writes[T]) = Writes[F :#: T] {
      case head :#: tail =>

        val h = FHead.writes(head)
        val t = FTail.writes(tail)

        (h, t) match {
          case (h: JsValue, JsNull)      => Json.obj(name -> h)
          case (JsNull, t: JsObject)     => t
          case (h: JsValue, t: JsObject) => Json.obj(name -> h) ++ t
          case _                         => Json.obj()
        }
    }
    def project[F, G](instance: => Writes[G], to: F => G, from: G => F) = Writes[F]{ f =>
      instance.writes(to(f))
    }

    def emptyCoproduct: Writes[CNil] = Writes(_ => JsNull)

    def coproduct[L, R <: Coproduct](name: String, cl: => Writes[L], cr: => Writes[R]) = Writes[L :+: R]{ lr =>

      val r = lr match {
        case Inl(left)  => cl writes left
        case Inr(right) => cr writes right
      }
      r match {
        case JsNull => JsString(name)
        case o      => o
      }
    }
  }
}

object SReads extends LabelledTypeClassCompanion[Reads] {
  object typeClass extends LabelledTypeClass[Reads] {

    def emptyProduct: Reads[HNil] = Reads(_ => JsSuccess(HNil))

    def product[F, T <: HList](name: String, FHead: Reads[F], FTail: Reads[T]) = Reads[F :#: T] {
      case obj @ JsObject(fields) =>
        for {
          head <- FHead.reads(obj \ name)
          tail <- FTail.reads(obj - name)
        } yield head :: tail

      case _ => JsError("Json object required")
    }

    def project[F, G](instance: => Reads[G], to: F => G, from: G => F) = Reads[F](instance.map(from).reads)

    def emptyCoproduct: Reads[CNil] = Reads[CNil](_ => JsError("CNil object not available"))

    def coproduct[L, R <: Coproduct](name: String, cl: => Reads[L], cr: => Reads[R]) = Reads[L :+: R]{ js =>

      js match {
        case js @ JsString(n) if n == name => cl.reads(js).map(Inl.apply)
        case js @ _                         => cr.reads(js).map(Inr.apply)
      }
    }
  }
}

object SFormats extends LabelledTypeClassCompanion[Format] {
  object typeClass extends LabelledTypeClass[Format] {
    def emptyProduct: Format[HNil] = Format(
      SReads.typeClass.emptyProduct,
      SWrites.typeClass.emptyProduct
    )

    def product[F, T <: HList](name: String, FHead: Format[F], FTail: Format[T]) = Format[F :#: T] (
      SReads.typeClass.product[F, T](name, FHead, FTail),
      SWrites.typeClass.product[F, T](name, FHead, FTail)
    )

    def project[F, G](instance: => Format[G], to: F => G, from: G => F) = Format[F](
      SReads.typeClass.project(instance, to, from),
      SWrites.typeClass.project(instance, to, from)
    )

    def emptyCoproduct = Format[CNil](
      SReads.typeClass.emptyCoproduct,
      SWrites.typeClass.emptyCoproduct
    )

    def coproduct[L, R <: shapeless.Coproduct](name: String, cl: => Format[L], cr: => Format[R]) = Format[L :+: R](
      SReads.typeClass.coproduct(name, cl, cr),
      SWrites.typeClass.coproduct(name, cl, cr)
    )
  }
}

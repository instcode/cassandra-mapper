package domain

import java.util.UUID

import core.Mapper

case class Test4(
   id: UUID,
   text1: String,
   text2: String,
   text3: String,
   text4: String)

case class Test23(
   id: UUID,
   text1: String,
   text2: String,
   text3: String,
   text4: String,
   text5: String,
   text6: String,
   text7: String,
   text8: String,
   text9: String,
   text10: String,
   text11: String,
   text12: String,
   text13: String,
   text14: String,
   text15: String,
   text16: String,
   text17: String,
   text18: String,
   text19: String,
   text20: String,
   text21: String,
   text22: String,
   text23: String
   )

object Test {
  implicit val mapper4 = new Mapper[Test4]()
  implicit val mapper23 = new Mapper[Test23]()
}

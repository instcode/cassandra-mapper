package core

import com.datastax.driver.core.{ProtocolVersion, Row}
import util.ReflectionUtil

import scala.reflect.runtime.universe._

/**
 * @author instcode
 */
class Mapper[T: TypeTag] {

  val columnNames = ReflectionUtil.constructorParams[T].map(_._1)

  def map(r: Row)(implicit protocolVersion: ProtocolVersion): T = {
    val columnDefinitions = r.getColumnDefinitions
    val data = new Array[Object](columnNames.size)
    for (i <- 0 to columnNames.size - 1) {
      val name = columnNames(i)
      data(i) = columnDefinitions.getType(i).deserialize(r.getBytesUnsafe(name), protocolVersion)
      println(i + ": " + name + " ---> " + data(i))
    }
    ReflectionUtil.create[T](data)
  }
}

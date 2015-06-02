package core

import java.util.UUID

import domain.{Test4, Test23}
import org.specs2.mutable.Specification
import play.api.libs.json.{Json, Writes}
import util.SWrites

import scala.io.Source
import scala.util.Try

class EverythingSpec extends Specification with ConfigCassandraCluster {
  val session = cluster.connect(Keyspaces.test)
  Try {
    val query = Source.fromFile(getClass.getResource("/keyspaces.cql").getFile).mkString
    query.split(";").foreach(session.execute)
  }

  "A mapper" should {
    "map a 22+ fields C* row to a case class" in {

      // A round trip
      val test23 = Test23(UUID.randomUUID(),
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23")
      Main.insert23(session, test23)
      val result23 = Main.query23(session, test23.id)
      result23 == test23
    }

    "map a 4 field C* row to a case class" in {
      // A round trip
      val test4 = Test4(UUID.randomUUID(),
        "1", "2", "3", "4")
      Main.insert4(session, test4)
      val result4 = Main.query4(session, test4.id)
      result4 == test4
    }
  }

  "A json writer" should {
    "write a 22+ field case class to json correctly" in {
      implicit val testWrites23: Writes[Test23] = SWrites.deriveInstance
      val output23 = Test23(UUID.randomUUID(),
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23")
      val outputJson: String = Json.toJson(output23).toString()
      val expectedJson: String = "{\"id\":\"" + output23.id + "\",\"text1\":\"1\",\"text2\":\"2\",\"text3\":\"3\",\"text4\":\"4\",\"text5\":\"5\",\"text6\":\"6\",\"text7\":\"7\",\"text8\":\"8\",\"text9\":\"9\",\"text10\":\"10\",\"text11\":\"11\",\"text12\":\"12\",\"text13\":\"13\",\"text14\":\"14\",\"text15\":\"15\",\"text16\":\"16\",\"text17\":\"17\",\"text18\":\"18\",\"text19\":\"19\",\"text20\":\"20\",\"text21\":\"21\",\"text22\":\"22\",\"text23\":\"23\"}"
      println("Output 23: " + outputJson + "vs. " + expectedJson)
      outputJson.equals(expectedJson)
    }
  }
}
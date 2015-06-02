package core

import java.util.UUID

import _root_.util.SWrites
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{Session, ProtocolVersion}
import domain.{Test4, Test, Test23}
import play.api.libs.json._
import play.api.libs.json.Json

object Main extends App with ConfigCassandraCluster {

  val session = cluster.connect(Keyspaces.test)

  def insert4(session: Session, test: Test4): Unit = {
    val preparedStatement = session.prepare(
      "INSERT INTO test4(id, text1, text2, text3, text4) VALUES (?, ?, ?, ?, ?);")
    session.executeAsync(preparedStatement.bind(
      test.id,
      test.text1,
      test.text2,
      test.text3,
      test.text4)
    )
  }

  def insert23(session: Session, test: Test23): Unit = {
    val preparedStatement = session.prepare(
      "INSERT INTO test23(id, text1, text2, text3, text4, text5, text6, text7, text8, text9, text10, text11, text12, text13, text14, text15, text16, text17, text18, text19, text20, text21, text22, text23) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
    session.executeAsync(preparedStatement.bind(
      test.id,
      test.text1,
      test.text2,
      test.text3,
      test.text4,
      test.text5,
      test.text6,
      test.text7,
      test.text8,
      test.text9,
      test.text10,
      test.text11,
      test.text12,
      test.text13,
      test.text14,
      test.text15,
      test.text16,
      test.text17,
      test.text18,
      test.text19,
      test.text20,
      test.text21,
      test.text22,
      test.text23)
    )
  }

  def query4(session: Session, uuid: UUID) = {
    val query = QueryBuilder.select().from(Keyspaces.test, ColumnFamilies.test4).where(QueryBuilder.eq("id", uuid))
    val row = session.execute(query).one()
    Test.mapper4.map(row)(ProtocolVersion.V3)
  }

  def query23(session: Session, uuid: UUID) = {
    val query = QueryBuilder.select().from(Keyspaces.test, ColumnFamilies.test23).where(QueryBuilder.eq("id", uuid))
    val row = session.execute(query).one()
    Test.mapper23.map(row)(ProtocolVersion.V3)
  }

  val test23 = Test23(UUID.fromString("387b57af-25fd-4789-8a98-92fe30b76788"),
    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
    "21", "22", "23")
  insert23(session, test23)

  val output23: Test23 = query23(session, UUID.fromString("387b57af-25fd-4789-8a98-92fe30b76788"))

  implicit val testWrites23: Writes[Test23] = SWrites.deriveInstance

  println("Output 23: " + Json.toJson(output23))
  
  cluster.close()
  System.exit(0)
}

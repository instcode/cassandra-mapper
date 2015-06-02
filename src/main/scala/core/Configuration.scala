package core

import com.typesafe.config.ConfigFactory
import com.datastax.driver.core.{ProtocolOptions, Cluster}

trait CassandraCluster {
  def cluster: Cluster
}

trait ConfigCassandraCluster extends CassandraCluster {

  private def config = ConfigFactory.load()

  import scala.collection.JavaConversions._
  private val cassandraConfig = config.getConfig("test.db.cassandra")
  private val port = cassandraConfig.getInt("port")
  private val hosts = cassandraConfig.getStringList("hosts").toList

  lazy val cluster: Cluster =
    Cluster.builder().
      addContactPoints(hosts: _*).
      withCompression(ProtocolOptions.Compression.SNAPPY).
      withPort(port).
      build()
}
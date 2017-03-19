import java.io.{FileInputStream, StringWriter}
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.util.ByteString
import com.drewhk.stream.xml.Xml
import com.drewhk.stream.xml.Xml.{EndDocument, StartDocument, StartElement}
import com.typesafe.config.ConfigFactory
import formatters.DocumentParser
import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.BOMInputStream
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.{ExecutionContext, Future}


/**
  * this class will read a file from local file system from resources and push to Kafka
  */
class ContentProducer {
  implicit val system = ActorSystem("kafka-producer")
  implicit val materializer = ActorMaterializer()

  //ConfigFactory.load()

  /*val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:2181")*/



  def run(implicit executor: ExecutionContext): Unit = {
    val writer = new StringWriter()
    val parse = Flow[ByteString]
      .via(Xml.parser)/*.map{
        case StartDocument => writer.write("[")
        case EndDocument => writer.write("]")
        case StartElement(label, attributes) => writer.write(s"""{"$label" : """)
    }*/
      .via(Flow.fromGraph(new DocumentParser))
      .toMat(Sink.seq)(Keep.both)

    val source = Source.single(ByteString(IOUtils.toByteArray(new BOMInputStream(
      new FileInputStream("/Users/jkn7485/Repos/alpakka-akka-kafka-integration/src/main/resources/journalArticles.xml"))
    )))
    source.runWith(parse)._2.map(a => println(a.head))
    //source.via(parse).runWith(Sink.ignore)
  }

}

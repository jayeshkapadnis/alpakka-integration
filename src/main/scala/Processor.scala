import java.io.FileInputStream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import model.{JournalArticle, JournalArticleIterator}
import org.apache.commons.io.input.BOMInputStream

import scala.concurrent.Future

object Processor extends App{

    implicit val system = ActorSystem("music-articles")
    implicit val materializer = ActorMaterializer()
    import system.dispatcher


  def source(filename:String): Source[JournalArticle, NotUsed] = {
    val fis = new FileInputStream(filename)
    val iter = new JournalArticleIterator(fis)
    Source.fromIterator (() => iter)
  }

    /*val numCPUs = Runtime.getRuntime().availableProcessors()

    val sink = Sink.ignore/*fold(0)((x:Int, y:ArticleType.Value) => {
      if ((x % 25) == 0) {
        println(x)
      }
      x + 1
    })*/
  val fis = new FileInputStream("/Users/jkn7485/Repos/alpakka-akka-kafka-integration/src/main/resources/journalArticles.xml")
  val iter = new JournalArticleIterator(new BOMInputStream(fis))
  //val src = Source.fromIterator (() => iter)
    //val src = source("/Users/jkn7485/Repos/alpakka-akka-kafka-integration/src/main/resources/journalArticles.xml")
    Source.fromIterator(() => iter).runWith(Sink.foreach(println))*/

  val contentProducer = new ContentProducer

  contentProducer.run


}

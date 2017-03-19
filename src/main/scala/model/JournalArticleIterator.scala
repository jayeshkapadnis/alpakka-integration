package model

import java.io.InputStream
import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.xml.pull.{EvElemEnd, EvElemStart, EvText, XMLEventReader}

class JournalArticleIterator(inputStream: InputStream) extends Iterator[JournalArticle]{
  val xml = new XMLEventReader(Source.fromInputStream(inputStream))

  var article : Option[JournalArticle] = parse

  def parse: Option[JournalArticle] = {
    var documentFinished = false
    def loop(currNode: List[String]) {
      if (!documentFinished && xml.hasNext) {
        xml.next match {
          case EvElemStart(_, label, attrs, _) =>
            processAttributes(attrs.asAttrMap, currNode)
            loop(label :: currNode)
          case EvElemEnd(_, label) if !label.equals("Document") =>
            println("End element: " + label)
            loop(currNode.tail)
          case EvElemEnd(_, "Document") =>
            documentFinished = true
            loop(currNode.tail)
          case EvText(text) =>
            println("Text nodes: "+currNode)
            processText(text, currNode)
            loop(currNode)
          case _ =>
            loop(currNode)
        }
      }
    }
    loop(List.empty)
    Some(JournalArticle())
  }

  var authors: ListBuffer[Author] = ListBuffer()

  def processText(text: String, nodes: List[String]): Unit = {
    nodes match {
      case List("Author", "Authors", "Article", "Document") => authors += Author(text)
      case _ =>
    }
  }

  def processAttributes(attrs: Map[String, String], nodes: List[String]): Unit = {
    nodes match {
      case List("Citation", "Article", "Document", "Package") =>
    }
  }

  // Proxy the state of the Option monad
  def hasNext = xml.hasNext

  def next() : JournalArticle = {
    //documentFinished = false
    val content = article.get
    article = parse
    content
  }
}

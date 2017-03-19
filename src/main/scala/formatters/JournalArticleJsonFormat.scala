package formatters

import spray.json._
import scala.xml._


class JournalArticleJsonFormat {

  implicit object NodeFormat extends JsonWriter[Node] {

    def write(node: Node) =
      if (node.child.count(_.isInstanceOf[Text]) == 1)
        JsString(node.text)
      else
        JsObject(node.child.collect {
          case e: Elem => e.label -> write(e)
        }: _*)
  }
}
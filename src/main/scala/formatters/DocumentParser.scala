package formatters

import java.io._
import javax.xml.stream.{XMLEventWriter, XMLStreamException}

import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler, OutHandler}
import com.drewhk.stream.xml.Xml.StartDocument
import com.drewhk.stream.xml.Xml._
import de.odysseus.staxon.json.{JsonXMLConfig, JsonXMLConfigBuilder, JsonXMLOutputFactory, JsonXMLStreamWriter}

class DocumentParser extends GraphStage[FlowShape[ParseEvent, String]]{

  val in: Inlet[ParseEvent] = Inlet("XMLDocument.in")
  val out: Outlet[String] = Outlet("XMLDocument.out")
  val config: JsonXMLConfig = new JsonXMLConfigBuilder()
                                  .autoArray(false)
                                  .autoPrimitive(false)
                                  .prettyPrint(true)
                                  .build()
  private val jsonXMLOutputFactory = new JsonXMLOutputFactory(config)
  private var writer = new FileWriter("/Users/jkn7485/Repos/alpakka-akka-kafka-integration/src/main/resources/sample.json")//new StringWriter()
  private var streamWriter: JsonXMLStreamWriter = jsonXMLOutputFactory.createXMLStreamWriter(writer)
  private val eventWriter: XMLEventWriter = jsonXMLOutputFactory.createXMLEventWriter(streamWriter)
  override val shape: FlowShape[ParseEvent, String] = FlowShape(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with InHandler with OutHandler {
      private var isBuffering = false
      //private val buffer = new StringBuilder

      override def onPull(): Unit = pull(in)

      override def onPush(): Unit = {
        val event: ParseEvent = grab(in)
        event match {
          case StartElement(name, _) if name == "Document" =>
              isBuffering = true
              /*writer = new StringWriter()
              streamWriter = jsonXMLOutputFactory.createXMLStreamWriter(writer)*/
              write(event)
              pull(in)
          case EndElement(name) if name == "Document" =>
            write(event)
            isBuffering = false
            push(out, writer.toString)
            emit(out, writer.toString,
              () => if (isClosed(in)) completeStage())
            streamWriter.flush()
          case other =>
            write(other)
            pull(in)
        }
      }

      override def onUpstreamFinish(): Unit = {
        if (isBuffering) emit(out, writer.toString(), () => completeStage())
        else completeStage()
      }

      def write(event: ParseEvent): Unit = event match {
        case s: EndElement if s.localName == "Package" => writer.write("}}")
        case s: StartElement =>
          streamWriter.writeStartElement(s.localName)
          s.attributes.foreach(e =>
            streamWriter.writeAttribute(e._1, e._2)
          )
        case e: EndElement =>
          streamWriter.writeEndElement()
        case StartDocument =>
        case EndDocument =>
        case t: Characters => {
          streamWriter.writeCharacters(t.text)
        }
        case c: CData =>  streamWriter.writeCData(c.text)
        case cm: Comment => streamWriter.writeComment(cm.text)
        case p: ProcessingInstruction =>
          streamWriter.writeProcessingInstruction(p.target.getOrElse(""), p.data.orNull)
        case _ =>
          throw new XMLStreamException("Cannot write event " + event)
      }


      setHandlers(in, out, this)
    }
}

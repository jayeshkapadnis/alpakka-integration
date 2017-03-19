import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}

object Configurations {

  val config = ConfigFactory.load()

  def propsFromConfig(config: Config): Properties = {
    import scala.collection.JavaConversions._

    val props = new Properties()

    val map: Map[String, Object] = config.entrySet().map({ entry =>
      entry.getKey -> entry.getValue.unwrapped()
    })(collection.breakOut)

    props.putAll(map)
    props
  }
}



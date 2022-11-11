import com.typesafe.config.*
import org.rogach.scallop.ScallopConf


//!!Copied some code from ServerMain oblect
object ModuleScalaThree {
  def main(args: Array[String]): Unit = {
    println("Starting main server")
    val (port, host) = parseArgs(args.toList)
    println("host of server is " + host + " and port is " + port.toString)

  }

  def parseArgs(args: List[String]): (String, Int) = {
    import org.rogach.scallop.ScallopConfBase
    val appArgs = AppArgs(args)
    val host: String = appArgs.host.toOption.get
    val port = PortOperations.isPortAvailable(
      appArgs.port.toOption.get
    )
    (host, port)
  }

}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {

  import org.rogach.scallop.stringConverter
  import org.rogach.scallop.intConverter

  val port = opt[Int](required = false, default = Some(0))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}

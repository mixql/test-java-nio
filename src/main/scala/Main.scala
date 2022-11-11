import com.typesafe.config.*
import org.rogach.scallop.ScallopConf

import scala.sys.exit
import java.io.File

object ClientApp {
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

//  def clientSendMsgs(prefix: String = "3") =
//    for {
//      f <- ZioClientMsgs.ZioGrpcRemoteClient.sendZioMsgTest1(ZioMsgTest1("hello", "scala", prefix))
//      _ <- printLine(f.msg)
//      r <- ZioClientMsgs.ZioGrpcRemoteClient.sendZioMsgTest2Array(ZioMsgTest2Array(Seq("hello", "scala", prefix)))
//      _ <- printLine(r.msg)
//      r <- ZioClientMsgs.ZioGrpcRemoteClient.sendZioMsgTest3Map(ZioMsgTest3Map(
//        Map("msg1" -> "hello", "msg2" -> "scala", "msg3" -> prefix)
//      ))
//      _ <- printLine(r.msg)
//      _ <- printLine(s"Sending shutdown to scala $prefix module")
//      _ <- ZioClientMsgs.ZioGrpcRemoteClient.sendShutDown(ShutDown())
//      _ <- printLine(r.msg)
//    } yield ()
//
//  def StartModuleServer(moduleName: String, host: String, port: Int,
//                        basePath: File = new File(".")) = {
//    println(s"trying to  start module $moduleName at " + host + " and port at " + port +
//      " in " + basePath.getAbsolutePath
//    )
//    try {
//      ZIO.succeed(
//        CmdOperations.runCmdNoWait(
//          Some(s"$moduleName.bat --port $port --host $host"),
//          Some(s"$moduleName --port $port --host $host"), basePath))
//    } catch {
//      case e: Throwable => ZIO.fail(e)
//    }
//  }
//
//  def startClient(targetHost: String, targetPort: Int, moduleName: String = "") = {
//    if moduleName.isEmpty then
//      println("trying to  start client at " + targetHost + " and port at " + targetPort)
//    else println("trying to  start client at " + targetHost + " and port at " + targetPort +
//      " which will connect to module " + moduleName)
//
//    ZioClientMsgs.ZioGrpcRemoteClient.live(
//      ZManagedChannel(
//        ManagedChannelBuilder.forAddress(targetHost, targetPort).usePlaintext()
//      )
//    ).retry(Schedule.fixed(10.seconds) || Schedule.recurs(5))
//  }
//
//  override def run =
//    (for {
//      port1 <- PortOperations.isPortAvailable(0)
//      processServerScala2_12 <- StartModuleServer("module-scala2_12", "0.0.0.0", port1)
//      port2 <- PortOperations.isPortAvailable(0)
//      processServerScala3 <- StartModuleServer("module-scala3", "0.0.0.0", port2)
//      clientLayer2_12 <- ZIO.succeed(startClient("0.0.0.0", port1, "module-scala2_12"))
//      clientLayer3 <- ZIO.succeed(startClient("0.0.0.0", port2, "module-scala3"))
//      _ <- clientSendMsgs("2_12").provideLayer(clientLayer2_12)
//      _ <- clientSendMsgs("3").provideLayer(clientLayer3)
//      _ <- {
//        if (processServerScala2_12.isAlive()) {
//          processServerScala2_12.destroy()
//        }
//        if (processServerScala3.isAlive()) {
//          processServerScala3.destroy()
//        }
//        ZIO.succeed(0)
//      }
//
//    } yield ()).exitCode
}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {

  import org.rogach.scallop.stringConverter
  import org.rogach.scallop.intConverter

  val port = opt[Int](required = false, default = Some(0))
  val host = opt[String](required = false, default = Some("0.0.0.0"))
  verify()
}



import com.typesafe.config.*
import org.rogach.scallop.ScallopConf

import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.SocketChannel
import java.text.MessageFormat
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.Paths
import app.zio.grpc.remote.clientMsgs.*

import scala.annotation.tailrec


//!!Copied some code from ServerMain oblect
object ModuleScalaThree {
  //TO-DO Use selector or io.netty library here
  def main(args: Array[String]): Unit = {
    println("Starting main server")
    val (host, port) = parseArgs(args.toList)
    println("host of server is " + host + " and port is " + port.toString)
    var server: SocketChannel = null
    //select open

    try {
      server = tryToConnect(host, port, 5, 5000)
      //server.register(select)
      println("Connection established.")
      // for(;;){
        // switch select{
          // case readable => val msg1 = SocketOperations.readMsgFromSocket(server).asInstanceOf[ZioMsgTest1]
            // println(s"Recieved ZioMsgTest1 msg from server: ${msg1.msg} ${msg1.msg2} ${msg1.msg3}")
            // Future{spark.sql}.map{
              // SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Started successfully. Waiting for requests from you"))
            // }
          // case isDisconnected =>   
//            SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Started successfully. Waiting for requests from you"))
        // }
      }
      println("Send msg to server that i am alive")
      SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Started successfully. Waiting for requests from you"))
//      println("Waiting for ZioMsgTest1 from Server")
//      val msg1 = SocketOperations.readMsgFromSocket(server).asInstanceOf[ZioMsgTest1]
//      println(s"Recieved ZioMsgTest1 msg from server: ${msg1.msg} ${msg1.msg2} ${msg1.msg3}")
//      val msg2 = SocketOperations.readMsgFromSocket(server).asInstanceOf[ZioMsgTest2Array]
//      println(s"Recieved ZioMsgTest2Array msg from server: ${msg2.messages.mkString(" ")}")
//      val msg3 = SocketOperations.readMsgFromSocket(server).asInstanceOf[ZioMsgTest3Map]
//      println(s"Recieved ZioMsgTest3Map msg from server: ${msg3.msgMap.mkString(" ")}")
      while (server.isConnected) {}
    } catch {
      case ex: Exception =>
        println("Error: " + ex.getMessage)
    } finally {
      if server != null then
        server.close()
    }
  }

  def parseArgs(args: List[String]): (String, Int) = {
    import org.rogach.scallop.ScallopConfBase
    val appArgs = AppArgs(args)
    val host: String = appArgs.host.toOption.get
    val port = //PortOperations.isPortAvailable(
      appArgs.port.toOption.get
    //)
    (host, port)
  }

  @throws[Exception]
  @tailrec
  def tryToConnect(hostname: String, port: Integer, numberOfTries: Integer, timeout: Long): SocketChannel = {
    if (numberOfTries eq 0) throw new Exception("Number of tries are out. Stopped to connect to remote server's socket")
    var server: SocketChannel = null
    try {
      println("Trying to connect to remote server's socket")
      server = SocketChannel.open
      val socketAddr = new InetSocketAddress(hostname, port)
      server.connect(socketAddr)
      server
    } catch {
      case ex: Exception =>
        if (server != null) server.close()
        println(MessageFormat.format("Couls not connect to remote server socket. Sleep {0} millis", timeout))
        Thread.sleep(timeout)
        tryToConnect(hostname, port, numberOfTries - 1, timeout)
    }
  }

}

case class AppArgs(arguments: Seq[String]) extends ScallopConf(arguments) {

  import org.rogach.scallop.stringConverter
  import org.rogach.scallop.intConverter

  val port = opt[Int](required = true)
  val host = opt[String](required = true)
  verify()
}

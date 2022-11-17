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

object ModuleScalaThree {
  def main(args: Array[String]): Unit = {
    println("Client: Starting main client")
    val (host, port) = parseArgs(args.toList)
    println("Client: host of server is " + host + " and port is " + port.toString)
    var server: SocketChannel = null

    try {
      server = tryToConnect(host, port, 5, 5000)
      println("Client: Connection established.")
      println("Client: Send msg to server that i am alive")
      SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Client: Started successfully. Waiting for requests from you"))
      while (server.isConnected) {
        SocketOperations.readMsgFromSocket(server) match { //make readMsgFromSocket non blocking
          case ZioMsgTest1(msg, msg2, msg3, _) =>
            println(s"Client: Received ZioMsgTest1 msg from server: ${msg} ${msg2} ${msg3}")
            println("Client: Sending reply on ZioMsgTest1 msg")
            SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Client module-scala3: " +
              "successfully received ZioMsgTest1"))
          case ZioMsgTest2Array(messages, _) =>
            println(s"Received ZioMsgTest2Array msg from server: ${messages.mkString(" ")}")
            println("Client: Sending reply on ZioMsgTest2Array msg")
            SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Client module-scala3: " +
              "successfully received ZioMsgTest2Array"))
          case ZioMsgTest3Map(msgMap, _) =>
            println(s"Received ZioMsgTest3Map msg from server: ${msgMap.mkString(" ")}")
            println("Client: Sending reply on ZioMsgTest3Map msg")
            SocketOperations.writeMsgToSocket(server, ZioMsgTestReply("Client module-scala3: " +
              "successfully received ZioMsgTest3Map"))
        }
      }
    } catch {
      case ex: Exception =>
        println("Client: Error: " + ex.getMessage)
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
    if (numberOfTries eq 0) throw new Exception("Client: Number of tries are out. Stopped to connect to remote server's socket")
    var server: SocketChannel = null
    try {
      println("Client: Trying to connect to remote server's socket")
      server = SocketChannel.open
      val socketAddr = new InetSocketAddress(hostname, port)
      server.connect(socketAddr)
      server
    } catch {
      case _: Exception =>
        if (server != null) server.close()
        println(MessageFormat.format("Client: Could not connect to remote server socket. Sleep {0} millis", timeout))
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

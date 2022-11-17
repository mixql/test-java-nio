import ClientModule.modulesNum

import java.io.File
import app.zio.grpc.remote.clientMsgs.*

import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.channels.{ServerSocketChannel, SocketChannel}


object ClientModule {
  var server: ServerSocketChannel = null
  var socketAddr: InetSocketAddress = null
  var modulesNum: Int = 0
}

class ClientModule(name: String, host: String, port: Int, basePath: File) extends java.lang.AutoCloseable {

  var clientRemoteProcess: sys.process.Process = null
  var client: SocketChannel = null
  modulesNum = modulesNum + 1

  import ClientModule.*


  def sendMsg(msg: scalapb.GeneratedMessage): scalapb.GeneratedMessage = {
    if clientRemoteProcess == null then
      startModuleClient()
      println("server: Clientmodule " + name + " waiting for init msg")
      SocketOperations.readMsgFromSocket(client) match {
        //To-do Should be ClientMsgStartedSuccesss
        case ZioMsgTestReply(msg, _) => println(s"server: Got init msg from client $name: " + msg)
        case _: Any => throw Exception("server: Got unknown init message from client " + name)
      }
    end if

    SocketOperations.writeMsgToSocket(client, msg)
    SocketOperations.readMsgFromSocket(client)
  }

  // def sendMsgAsync(msg: scalapb.GeneratedMessage): Future[scalapb.GeneratedMessage] = {
  //   Future{
  //     sendMsg(msg)
  //   }
  // }

  def initServerSocket() = {
    if server == null then
      println(s"server: ClientModule $name. Initialising socket on server side for communicating with remote module")
      server = ServerSocketChannel.open()
      socketAddr = new InetSocketAddress(port)
      server.socket.bind(socketAddr)
  }

  def startModuleClient() = {
    if server == null then initServerSocket()
    println(s"server: trying to  start module $name at " + host + " and port at " + port +
      " in " + basePath.getAbsolutePath
    )
    clientRemoteProcess = CmdOperations.runCmdNoWait(
      Some(s"$name.bat --port $port --host $host"),
      Some(s"$name --port $port --host $host"), basePath)
    println("server: waiting for connection")
    client = server.accept()
    println(s"server: connected with ${client.getRemoteAddress.toString}")
  }

  override def close() = {
    println("Server: Executing close")
    modulesNum = modulesNum - 1
    if (client != null) {
      client.finishConnect()
      client.close()
    }
    if (clientRemoteProcess.isAlive()) clientRemoteProcess.exitValue()
    println("server: Remote client was shutdown")
    if (modulesNum <= 0) server.close()
  }
}

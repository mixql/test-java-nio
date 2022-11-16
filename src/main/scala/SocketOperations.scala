import java.nio.channels.{SocketChannel}

object SocketOperations {
  def readMsgFromSocket(client: SocketChannel): scalapb.GeneratedMessage = {
    import java.nio.ByteBuffer
    val buffer = ByteBuffer.allocate(8)
    //    val buffer = ByteBuffer.allocate(1024)
    var protoBufMsg: Array[Byte] = Array()

    //Block and wait for answer
    var res = -1
    while (res < 0) {
      buffer.clear
      res = client.read(buffer)
      println("server: readMsgFromSocket: " + res)
    }
    protoBufMsg = protoBufMsg ++ buffer.array()
    buffer.clear

    while (res > 0) {
      //       buffer.flip
      //            fileChannel.write(buffer)

      res = client.read(buffer)
      println("server: readMsgFromSocket: " + res)
      protoBufMsg = protoBufMsg ++ buffer.array()
      buffer.clear
    }
    println("server: readMsgFromSocket: File Received. Converting array of bytes of size " + protoBufMsg.length + " to Protobuf msg")
    ProtoBufConverter.toProtobuf(protoBufMsg)
  }

  def writeMsgToSocket(client: SocketChannel, msg: scalapb.GeneratedMessage) = {
    var protoBufMsg: Array[Byte] = ProtoBufConverter.toArray(msg)
    println("\"server: \" writeMsgToSocket: size of protobufMsg: " + protoBufMsg.length)
    import java.nio.ByteBuffer
    val buffer = ByteBuffer.wrap(protoBufMsg)
    val res = client.write(buffer)
    System.out.println("server: " + res)
    buffer.clear
    System.out.println("server: File was Sent")
  }
}

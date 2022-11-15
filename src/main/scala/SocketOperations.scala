import java.nio.channels.{SocketChannel}

object SocketOperations {
  def readMsgFromSocket(client: SocketChannel): scalapb.GeneratedMessage = {
    import java.nio.ByteBuffer
    val buffer = ByteBuffer.allocate(1024)
    var protoBufMsg: Array[Byte] = Array()

    //Block and wait for answer
    var res = -1
    while (res < 0) {
      buffer.clear
      res = client.read(buffer)
      println(res)
    }

    while (res > 0) {
      // buffer.flip
      //      fileChannel.write(buffer)
      protoBufMsg = protoBufMsg ++ buffer.array()
      buffer.clear
      res = client.read(buffer)
      println(res)
    }
    println("File Received. Converting array of bytes of size " + protoBufMsg.length + " to Protobuf msg")
    ProtoBufConverter.toProtobuf(protoBufMsg)
  }

  def writeMsgToSocket(client: SocketChannel, msg: scalapb.GeneratedMessage) = {
    var protoBufMsg: Array[Byte] = ProtoBufConverter.toArray(msg)
    import java.nio.ByteBuffer
    val buffer = ByteBuffer.allocate(protoBufMsg.length).put(protoBufMsg)
    val res = client.write(buffer)
    System.out.println(res)
    buffer.clear
    System.out.println("File Sent")
  }
}

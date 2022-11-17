import java.nio.channels.{SocketChannel}
import java.nio.ByteBuffer

//TO_DO Use onebuffer, using flip and clear functions
object SocketOperations {
  def readMsgFromSocket(client: SocketChannel): scalapb.GeneratedMessage = {
    val protoBufMsgLength = readIntFromSocket(client)
    val protoBufMsg: Array[Byte] = readProtoBufFromSocket(client, protoBufMsgLength)
    println("client: readMsgFromSocket: File Received. Converting array of bytes of size " +
      protoBufMsg.length + " to Protobuf msg")
    ProtoBufConverter.toProtobuf(protoBufMsg)
  }

  def readIntFromSocket(client: SocketChannel): Int = {
    import java.nio.ByteBuffer
    val bytes = new Array[Byte](4)
    val buffer = ByteBuffer.wrap(bytes)
    //Block and wait for answer
    //    var res = -1
    //    while (res < 0) {
    buffer.clear
    val res = client.read(buffer)
    println("client: readIntSocket: " + res)
    if (res == -1) throw Exception("Client: Connection was closed")
    //    }
    val returnValue: Int = convertByteArrayToInt(bytes)
    buffer.clear
    println("client: readIntSocket: got int " + returnValue)
    returnValue
  }

  def readProtoBufFromSocket(client: SocketChannel, length: Int): Array[Byte] = {
    val buffer = ByteBuffer.allocate(length)

    //Block and wait for answer
    var res = -1
    //    while (res < 0) {
    buffer.clear
    res = client.read(buffer)
    println("client: readMsgFromSocket: " + res)
    //    }
    if (res == -1) throw Exception("Client: Connection was closed")
    val protoBufMsg: Array[Byte] = buffer.array()
    buffer.clear
    protoBufMsg
  }

  def writeMsgToSocket(client: SocketChannel, msg: scalapb.GeneratedMessage) = {
    System.out.println(s"client: convert ${msg.getClass.getName} to array of bytes")
    var protoBufMsg: Array[Byte] = ProtoBufConverter.toArray(msg)
    println("\"client: \" writeMsgToSocket: size of protobufMsg: " + protoBufMsg.length)
    writeIntToSocket(client, protoBufMsg.length)
    writeProtoBufToSocket(client, protoBufMsg)
    System.out.println(s"client: File ${msg.getClass.getName} was Sent")
  }

  def writeIntToSocket(client: SocketChannel, i: Int) = {
    System.out.println("client: Sending int to server")
    import java.nio.ByteBuffer
    val bb = ByteBuffer.wrap(intToBytes(i))
    val res = client.write(bb)
    System.out.println("client: " + res)
    bb.clear
    System.out.println("client: Int was Sent")
  }

  def writeProtoBufToSocket(client: SocketChannel, protoBufMsg: Array[Byte]) = {
    import java.nio.ByteBuffer
    val buffer = ByteBuffer.wrap(protoBufMsg)
    val res = client.write(buffer)
    System.out.println("client: " + res)
    buffer.clear
  }

  def intToBytes(data: Int) = Array[Byte](
    ((data >> 24) & 0xff).toByte,
    ((data >> 16) & 0xff).toByte,
    ((data >> 8) & 0xff).toByte,
    ((data >> 0) & 0xff).toByte
  )

  def convertByteArrayToInt(data: Array[Byte]): Int = {
    if (data == null || data.length != 4) return 0x0
    // ----------
    ((0xff & data(0)) << 24 | (0xff & data(1)) << 16 | (0xff & data(2)) << 8 | (0xff & data(3)) << 0).toInt
  }
}

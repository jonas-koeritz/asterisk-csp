package opencsp.uacsta;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class SipPacketDecoder extends ByteToMessageDecoder {
    private String headerDelimiter = "\r\n\r\n";
    private StringBuilder currentPacket = new StringBuilder();

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        ByteBuf buffer = Unpooled.buffer();
        currentPacket.append(in.readBytes(buffer).toString(CharsetUtil.UTF_8));
        //Is there a complete header?
        if(currentPacket.indexOf(headerDelimiter) >= 0) {

        }

        out.add(currentPacket);
        currentPacket = new StringBuilder();
    }
}

package opencsp.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import opencsp.Log;
import opencsp.exceptions.UnsupportedMessageFormatException;

import java.util.List;

public class CstaTcpMessageDecoder extends ByteToMessageDecoder {
    private static final String TAG = "CstaTcpMessageDecoder";

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) {
        //At least a CSTA TCP prefix must be present
        if(in.readableBytes() < 8) {
            return;
        }

        //One or more complete messages are inside the buffer
        if(in.readableBytes() >= in.getShort(2)) {
            try {
                CstaTcpMessage msg = new CstaTcpMessage(in.readBytes(in.getShort(2)));
                Log.d(TAG, "Received CSTA Message: " + msg.toString());
                out.add(msg);
            } catch (UnsupportedMessageFormatException ex) {
                Log.e(TAG, ex.getMessage());
                in.readBytes(in.getShort(2));
            }
        }
    }
}

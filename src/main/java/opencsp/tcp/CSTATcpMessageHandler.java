package opencsp.tcp;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class CSTATcpMessageHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext context, Object cstaTcpMessage) {
        CSTATcpMessage message = (CSTATcpMessage)cstaTcpMessage;

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {

    }
}

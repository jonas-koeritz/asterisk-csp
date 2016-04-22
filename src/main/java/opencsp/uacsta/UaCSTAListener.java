package opencsp.uacsta;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import opencsp.Log;


public class UaCSTAListener {
    private static final String TAG = "UaCSTAListener";
    private int port = 6060;
    private Channel channel;

    private UaCSTAProvider provider;

    public UaCSTAListener(int port, UaCSTAProvider provider) {
        this.port = port;
        this.provider = provider;
    }

    public void startListening() {
        Log.i(TAG, "Starting uaCSTA Listener on Port " + port);
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        public void initChannel(DatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new SipPacketDecoder());
                            ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new UaCSTAMessageDecoder());
                            ch.pipeline().addLast(new UaCSTAMessageHandler());
                        }
                    });

            channel = b.bind(port).sync().channel();

            channel.closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

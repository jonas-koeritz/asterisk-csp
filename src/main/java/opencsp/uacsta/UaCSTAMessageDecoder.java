package opencsp.uacsta;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import opencsp.Log;

import java.util.List;


public class UaCSTAMessageDecoder extends MessageToMessageDecoder<String> {
    private static final String TAG = "UaCSTAMessageDecoder";

    @Override
    public void decode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
        Log.d(TAG, "Decoding uaCSTA Message:\n" + message);
    }
}

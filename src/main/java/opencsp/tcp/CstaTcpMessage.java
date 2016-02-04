package opencsp.tcp;

import io.netty.buffer.ByteBuf;
import opencsp.exceptions.UnsupportedMessageFormatException;

import java.nio.charset.Charset;


public class CstaTcpMessage {
    private static final String TAG = "CstaTcpMessage";

    private int invokeId;
    private int length;
    private String body;

    private static Charset UTF8 = Charset.forName("UTF-8");

    public CstaTcpMessage(int invokeId, int length, String body) {
        this.invokeId = invokeId;
        this.length = length;
        this.body = body;
    }

    public CstaTcpMessage(ByteBuf rawData) throws UnsupportedMessageFormatException {
        if(rawData.getByte(0) == 0x00) {
            this.invokeId = Integer.parseInt(
                    rawData.toString(4, 4, UTF8)
            );
            this.length = rawData.getShort(2);
            this.body = rawData.toString(8, this.length - 8, UTF8);
        } else {
            throw new UnsupportedMessageFormatException();
        }
    }

    public CstaTcpMessage(int invokeId, String body) {
        this.invokeId = invokeId;
        this.length = body.length() + 8;
        this.body = body;
    }

    public String toString() {
        return "[CstaTcpMessage: invokeId=" + invokeId + ", length=" + length + ", body=" + body + "]";
    }
}

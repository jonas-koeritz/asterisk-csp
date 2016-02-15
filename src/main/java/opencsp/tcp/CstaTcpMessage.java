package opencsp.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import opencsp.exceptions.UnsupportedMessageFormatException;
import java.nio.charset.Charset;


public class CSTATcpMessage {
    private static final String TAG = "CSTATcpMessage";
    public static final int EVENT_INVOKE_ID = 9999;

    private int invokeId;
    private int length;
    private String body;

    private static Charset UTF8 = Charset.forName("UTF-8");

    public CSTATcpMessage(int invokeId, int length, String body) {
        this.invokeId = invokeId;
        this.length = length;
        this.body = body;
    }

    public CSTATcpMessage(ByteBuf rawData) throws UnsupportedMessageFormatException {
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

    public ByteBuf toByteBuf() {
        ByteBuf output = Unpooled.buffer();
        output.writeByte(0x00);
        output.writeByte(0x00);
        output.writeShort(this.length);

        String invokeId = String.format("%04d", this.invokeId);
        output.writeBytes(invokeId.getBytes());
        output.writeBytes(this.body.getBytes());
        return output;
    }


    public CSTATcpMessage(int invokeId, String body) {
        this.invokeId = invokeId;
        this.length = body.length() + 8;
        this.body = body;
    }

    public int getInvokeId() {
        return invokeId;
    }

    public String toString() {
        return "[CSTATcpMessage: invokeId=" + invokeId + ", length=" + length + ", body=" + body + "]";
    }

    public String getBody() {
        return body;
    }
}

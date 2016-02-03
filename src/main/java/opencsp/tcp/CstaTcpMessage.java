package opencsp.tcp;

import io.netty.buffer.ByteBuf;
import opencsp.Log;
import opencsp.UnsupportedMessageFormatException;
import opencsp.csta.types.CSTAMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;


public class CstaTcpMessage {
    private static final String TAG = "CstaTcpMessage";

    private int invokeId;
    private int length;
    private String body;
    private CSTAMessage parsedMessage;

    private static Charset UTF8 = Charset.forName("UTF-8");

    public CstaTcpMessage(int invokeId, int length, String body) {
        this.invokeId = invokeId;
        this.length = length;
        this.body = body;

        parseMessage();
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

        parseMessage();
    }

    private void parseMessage() {
        try {
            Document doc = toDocument(body);
            if (doc != null) {
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList rootNode = (NodeList) xPath.evaluate("/*", doc.getDocumentElement(), XPathConstants.NODESET);
                if(rootNode.getLength() == 1) {
                    String messageType = rootNode.item(0).getNodeName();
                    Class<?> messageClass = Class.forName("opencsp.csta.messages." + messageType);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }
    }

    private Document toDocument(String xml) throws Exception {
        DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(new InputSource(new StringReader(xml)));
    }

    public String toString() {
        return "[CstaTcpMessage: invokeId=" + invokeId + ", length=" + length + ", body=" + body + "]";
    }
}

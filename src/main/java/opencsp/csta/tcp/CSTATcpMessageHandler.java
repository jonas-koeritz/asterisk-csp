package opencsp.csta.tcp;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.csta.types.CSTAMessage;
import opencsp.util.ClassFinder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.List;

public class CSTATcpMessageHandler extends ChannelHandlerAdapter {
    private static final String TAG = "CSTATcpMessageHandler";

    List<Class<?>> messageClasses;

    DocumentBuilder documentBuilder;

    private Provider provider;

    public CSTATcpMessageHandler(Provider provider) {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        messageClasses = ClassFinder.find("opencsp.csta.messages");
        this.provider = provider;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object cstaTcpMessage) {
        CSTATcpMessage message = (CSTATcpMessage)cstaTcpMessage;
        try {
            Document xml = documentBuilder.parse(new InputSource(new StringReader(message.getBody())));
            String messageType = xml.getDocumentElement().getNodeName();



            if( messageClasses.stream().filter(c -> c.getSimpleName().equals(messageType)).count() > 0) {
                Class messageClass = messageClasses.stream().filter(c -> c.getSimpleName().equals(messageType)).findFirst().get();
                CSTAMessage parsedMessage = null;

                Constructor[] availableConstructors = messageClass.getDeclaredConstructors();
                for (Constructor ctor : availableConstructors) {
                    Class<?>[] parameterTypes = ctor.getParameterTypes();
                    if (parameterTypes.length == 1 && parameterTypes[0].equals(String.class)) {
                        parsedMessage = (CSTAMessage) ctor.newInstance(message.getBody());
                    }
                }

                if (parsedMessage == null) {
                    Log.e(TAG, "No Message Class that could parse message type " + messageType);
                } else {
                    provider.handleMessage(message.getInvokeId(), parsedMessage, context.channel());
                }
            } else {
                Log.e(TAG, "No Message class for messageType " + messageType + " found");
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {

    }
}

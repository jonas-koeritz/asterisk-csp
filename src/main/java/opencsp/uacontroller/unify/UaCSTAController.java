package opencsp.uacontroller.unify;

import opencsp.Log;
import opencsp.csta.Provider;
import opencsp.devices.SIPPhone;
import opencsp.uacontroller.UAController;
import opencsp.util.ConfigurationProvider;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UaCSTAController implements UAController {
    public static final String TAG = "UaCSTAController";
    public static final String TYPE = "unify";

    private String ipAddress;
    private int port;
    private String deviceId;
    private String localIp;
    private String localSipPort;

    public UaCSTAController(SIPPhone phone, ConfigurationProvider config) {
        Log.d(TAG, "Creating new UaCSTAController for phone " + phone.toString());
        ipAddress = phone.getIpAddress();
        port = phone.getPort();
        deviceId = phone.getDeviceId().toString();
        localIp = config.getConfigurationValue("local_ip");
        localSipPort = config.getConfigurationValue("local_sip_port");
    }

    private void transmitMessage(String message) {
        Log.d(TAG, "Transmitting message to phone " + deviceId + " at " + ipAddress + ":" + port);
        Log.d(TAG, "\r" + message);
        try {
            InetAddress phoneIp = InetAddress.getByName(ipAddress);
            byte[] data = message.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, phoneIp, port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void makeCall(String calledDirectoryNumber) {
        StringBuilder msg = new StringBuilder(prepareSipMessage());

        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        body.append("<MakeCall xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">");
        body.append("<callingDevice>").append(deviceId).append("</callingDevice>");
        body.append("<calledDirectoryNumber>").append(calledDirectoryNumber).append("</calledDirectoryNumber>");
        body.append("<autoOriginate>doNotPrompt</autoOriginate>");
        body.append("</MakeCall>\r\n");

        msg.append("Content-Length: ").append(body.length()).append("\r\n\r\n");
        msg.append(body.toString());

        transmitMessage(msg.toString());
    }



    @Override
    public void answerCall() {
        StringBuilder msg = new StringBuilder(prepareSipMessage());

        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        body.append("<AnswerCall xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">");
        body.append("<callToBeAnswered><deviceID>").append(deviceId).append("</deviceID></callToBeAnswered>");
        body.append("</AnswerCall>\r\n");

        msg.append("Content-Length: ").append(body.length()).append("\r\n\r\n");
        msg.append(body.toString());

        transmitMessage(msg.toString());
    }

    @Override
    public void clearConnection() {
        StringBuilder msg = new StringBuilder(prepareSipMessage());

        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        body.append("<ClearConnection xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">");
        body.append("<connectionToBeCleared><callID></callID><deviceID>").append(deviceId).append("</deviceID></connectionToBeCleared>");
        body.append("</ClearConnection>\r\n");

        msg.append("Content-Length: ").append(body.length()).append("\r\n\r\n");
        msg.append(body.toString());

        transmitMessage(msg.toString());
    }

    @Override
    public void holdCall() {
        StringBuilder msg = new StringBuilder(prepareSipMessage());

        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        body.append("<HoldCall xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">");
        body.append("<callToBeHeld><callID></callID><deviceID>").append(deviceId).append("</deviceID></callToBeHeld>");
        body.append("</HoldCall>\r\n");

        msg.append("Content-Length: ").append(body.length()).append("\r\n\r\n");
        msg.append(body.toString());

        transmitMessage(msg.toString());
    }

    @Override
    public void retrieveCall() {
        StringBuilder msg = new StringBuilder(prepareSipMessage());

        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        body.append("<RetrieveCall xmlns=\"http://www.ecma-international.org/standards/ecma-323/csta/ed3\">");
        body.append("<callToBeRetrieved><callID></callID><deviceID>").append(deviceId).append("</deviceID></callToBeRetrieved>");
        body.append("</RetrieveCall>\r\n");

        msg.append("Content-Length: ").append(body.length()).append("\r\n\r\n");
        msg.append(body.toString());

        transmitMessage(msg.toString());
    }

    private String prepareSipMessage() {
        StringBuilder msg = new StringBuilder();
        msg.append("NOTIFY sip:").append(deviceId).append("@").append(ipAddress).append(":").append(port)
                .append(";transport=udp SIP/2.0\r\n");
        msg.append("VIA: SIP/2.0/UDP ").append(localIp).append(":").append(localSipPort).append(";branch=z01235\r\n");
        msg.append("From: <sip:").append(deviceId).append("@").append(ipAddress).append(";transport=udp>;tag=e66CkC8b6\r\n");
        msg.append("To: <sip:").append(deviceId).append("@").append(ipAddress).append(";transport=udp>\r\n");
        msg.append("Call-ID: ").append((Math.random() * 10000) + 1).append("\r\n");
        msg.append("CSeq: 1 NOTIFY\r\n");
        msg.append("Contact: <sip:").append(deviceId).append("@").append(ipAddress).append(":").append(port).append(";transport=udp>\r\n");
        msg.append("Event: uaCSTA\r\n");
        msg.append("Max-Forwards: 69\r\n");
        msg.append("Content-Type: application/csta+xml\r\n");
        return msg.toString();
    }
}

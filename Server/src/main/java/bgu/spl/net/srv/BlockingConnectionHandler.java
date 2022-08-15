package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.impl.BGSServer.MessageEncoderDecoderImpl;
import bgu.spl.net.impl.BGSServer.ConnectionsImpl;
import bgu.spl.net.impl.BGSServer.Messages.Register;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;

    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

//Added:
    private int connectionId;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, int connectionId,ConnectionsImpl<T> connection) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connectionId = connectionId;
        this.protocol.start(connectionId, connection); //TODO:check
    }



    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());


            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                   protocol.process(nextMessage);

/**
      Unlike MessagingProtocol response are sent via connections object send function.
 */
//                    T response = protocol.process(nextMessage);
//                    if (response != null) {
//                        out.write(encdec.encode(response));
//                        out.flush();
//                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

//    public ConnectionsImpl<T> getConnections() {
//        return connections;
//    }

    @Override
    public synchronized void send(T msg) {
        try{
            out = new BufferedOutputStream(sock.getOutputStream());
            out.write(encdec.encode(msg));
            out.flush();
        }

        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

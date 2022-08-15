package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        DB data = DB.getInstance();
                Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new BidiMessagingProtocolImpl(data), //protocol factory
                () ->new MessageEncoderDecoderImpl() //message encoder decoder factory
        ).serve();
    }
}

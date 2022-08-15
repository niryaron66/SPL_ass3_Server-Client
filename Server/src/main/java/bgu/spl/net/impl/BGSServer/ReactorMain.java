package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.Messages.Message;
import bgu.spl.net.impl.newsfeed.NewsFeed;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        DB data = DB.getInstance(); //one shared object
        Server.reactor(
                Integer.parseInt(args[0]), // port
                Integer.parseInt(args[1]), // Num of threads
                () -> new BidiMessagingProtocolImpl(data), //protocol factory
                () ->new MessageEncoderDecoderImpl() //message encoder decoder factory
        ).serve();
    }
}

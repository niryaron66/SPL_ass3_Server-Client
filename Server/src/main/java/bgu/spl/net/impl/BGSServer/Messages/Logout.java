package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;
import bgu.spl.net.impl.BGSServer.User;


public class Logout extends Message {
    private  final short OPCODE = 3;
    @Override
    public void process(int connectionId, Connections connections, DB database) {
        User user =  database.getLoggedInUser().get(connectionId);
        if (user != null && user.isLoggedIn()){
            user.logout(database , connectionId);
            connections.disconnect(connectionId);
            //send ACK
            ACK ackMessage = new ACK(OPCODE, null);
            connections.send(connectionId , ackMessage);
        }
        else{
            Error errorMessage = new Error(OPCODE);
            connections.send(connectionId , errorMessage);
        }
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}

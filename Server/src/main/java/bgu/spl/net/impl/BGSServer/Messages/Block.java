package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;
import bgu.spl.net.impl.BGSServer.User;

public class Block extends Message{
    private final short OPCODE = 12;
    private String username;
    public Block(String username) {
        this.username=username;
    }
    @Override
    public void process(int connectionId, Connections connections, DB database) {
        User user =  database.getLoggedInUser().get(connectionId);
        if(user==null || !database.getUserName_ConnectionID().containsKey(this.username) || user.isBlocked(database.getRegisterUsers().get(this.username)) ){
            Error errorMessage=new Error(OPCODE);
            connections.send(connectionId , errorMessage);
        }
        else
        {
            //unfollow each other
        int tmpConnectionID=database.getUserName_ConnectionID().get(this.username);
         database.getRegisterUsers().get(this.username).addBlock(user);//got Connection id
         database.unfollow(connectionId,this.username);
         database.unfollow(tmpConnectionID,user.getUsername());
         ACK ackMessage = new ACK(OPCODE, null);
         connections.send(connectionId , ackMessage);
        }

    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}

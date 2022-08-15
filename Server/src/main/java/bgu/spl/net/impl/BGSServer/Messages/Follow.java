package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;

import java.nio.charset.StandardCharsets;

public class Follow extends Message {
    private final short OPCODE = 4;
    private String followOrUn;
    private String username;

    public Follow(String followOrUn, String username) {
        this.followOrUn = followOrUn;
        this.username = username;
    }

    @Override
    public void process(int connectionId, Connections connections, DB database) {
        boolean command;
        if (followOrUn.equals("0")) { // follow command
            command = database.follow(connectionId, username);
        } else { // unfollow command
            command = database.unfollow(connectionId, username);
        }
        if (command) {
            ACK ackMessage;
            if (followOrUn.equals("0")) { // follow command
                ackMessage = new ACK(OPCODE, ('\0' + '0' + this.username + '\0').getBytes(StandardCharsets.UTF_8));
            } else {
                ackMessage = new ACK(OPCODE, ('\0' + '1' + this.username + '\0').getBytes(StandardCharsets.UTF_8));
            }
            connections.send(connectionId, ackMessage);
        } else {
            Error errorMessage = new Error(OPCODE);
            connections.send(connectionId, errorMessage);
        }
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}
    





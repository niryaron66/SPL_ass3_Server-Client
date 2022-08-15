package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;
import bgu.spl.net.impl.BGSServer.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends Message{

    private  final short OPCODE = 1;
    private String username;
    private String password;
    private Date birthday;
    private String pattern = "dd-MM-yyyy";
    private SimpleDateFormat format = new SimpleDateFormat(pattern);
    public Register(String username, String password, String birthday) {
        this.username = username;
        this.password = password;
        try {
            this.birthday = format.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void process(int connectionId, Connections connections, DB database) {
        if (!database.getUserName_ConnectionID().containsKey(this.username)) {
            User user = new User(username, password, birthday);
            database.registerClient(connectionId, user ,this.username);
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

package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;

import java.nio.charset.StandardCharsets;

import static bgu.spl.net.impl.BGSServer.MessageEncoderDecoderImpl.shortToBytes;

public class Error extends Message{
    private final short OPCODE = 11;
    private short messageOPcode;
    public Error(short messageOPcode) {
        this.messageOPcode = messageOPcode;
    }

    @Override
    public void process(int connectionId, Connections connections, DB database) {}

    public byte[] encode() {
        byte[] byteArray=new byte[5];
        byteArray[0]= shortToBytes(OPCODE)[0];
        byteArray[1]= shortToBytes(OPCODE)[1];
        byteArray[2]=shortToBytes(messageOPcode)[0];
        byteArray[3]=shortToBytes(messageOPcode)[1];
        byteArray[4]=(byte)';';
        return byteArray;
    }

    public String toString()  {
        return "ERROR " + messageOPcode;
    }

}


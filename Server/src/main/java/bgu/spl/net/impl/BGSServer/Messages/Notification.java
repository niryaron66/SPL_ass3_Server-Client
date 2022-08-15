package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;
import bgu.spl.net.impl.BGSServer.MessageEncoderDecoderImpl;

import java.nio.charset.StandardCharsets;

public class Notification extends Message{
    private final short OPCODE=9;
    private byte notificationType;
    private String postingUser;
    private String content;

    public Notification(byte notificationType, String postingUser, String content) {
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }


    @Override
    public void process(int connectionId, Connections connections, DB database) {

    }
    public String getContent()
    {
        return content;
    }
    public byte[] encode()
    {
        byte[] byteArray=new byte[6+postingUser.length()+content.length()];
        byte[] postUser=this.postingUser.getBytes(StandardCharsets.UTF_8);
        byte[] conTent=this.content.getBytes(StandardCharsets.UTF_8);
        byteArray[0]= MessageEncoderDecoderImpl.shortToBytes(OPCODE)[0];
        byteArray[1]=MessageEncoderDecoderImpl.shortToBytes(OPCODE)[1];
        byteArray[2]=notificationType;
        int k=3;
        for(int i=0;i<postingUser.length();i++)
        {
            byteArray[k++]=postUser[i];
        }
        byteArray[k++]=0;
        for(int j=0;j<content.length();j++)
        {
            byteArray[k++]=conTent[j];
        }
        byteArray[k++]=0;
        byteArray[k]=';';
       return byteArray;
    }

}

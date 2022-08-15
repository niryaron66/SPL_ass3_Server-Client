package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSServer.Messages.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

//Should be T or not??????????
public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {
    private short opcode;
    private int len = 0;
    private byte[] bytes = new byte[1 << 10];
    private byte[] opBytes = new byte[2];

    @Override
    public Message decodeNextByte(byte nextByte) {
        //  System.out.print(nextByte);
        if (len == 2) {
            opcode = bytesToShort(opBytes);
        }
        if (nextByte == ';') {
            return popMessage();

        }



        pushByte(nextByte);
        return null; //not a line yet
    }

    private void pushByte(byte nextByte) {
        if (len < 2) {
            opBytes[len++] = nextByte;
        } else {
            bytes[(len++) - 2] = nextByte;
        }
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

    }

    @Override
//    public byte[] encode(Message message) {
//        String result = message.toString();
//        return result.getBytes();
//    }
    public byte[] encode(Message message) {
        return message.encode();
    }

    private Message popMessage() {
        String result = null;
        try {
            result = new String(bytes, "UTF-8");
            Arrays.fill(bytes, (byte) '\0');
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] arguments = result.split("\0");
        len = 0;
        switch (opcode) {
            case 1: //register
                return new Register(arguments[0], arguments[1], arguments[2]);
            case 2:
                return new Login(arguments[0], arguments[1], arguments[2]);
            case 3:
                return new Logout();
            case 4:
                return new Follow(arguments[0], arguments[1]);
            case 5:
                return new Post(arguments[0]);
            case 6:
                return new PM(arguments[0], arguments[1],arguments[2]);
            case 7:
                return new Logstat();
            case 8:
                return new Stat(arguments[0]);
            case 12:
                return new Block(arguments[0]);

        }

        Arrays.fill(arguments, null);
        return null;
    }

    public static short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    public int changethissssss(byte[] byteArr) {
        String text = new String(byteArr, StandardCharsets.US_ASCII); // "129"
        int value = Integer.parseInt(text);
        return value;
    }

}


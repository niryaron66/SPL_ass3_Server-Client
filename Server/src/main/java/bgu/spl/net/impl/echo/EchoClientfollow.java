package bgu.spl.net.impl.echo;

import java.io.*;
import java.net.Socket;

public class EchoClientfollow {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            args = new String[]{"127.0.0.1", "040maor;"};
//            args = new String[]{"127.0.0.1", "01maor0abc017-11-98;"};
        }

        if (args.length < 2) {
            System.out.println("you must supply two arguments: host, message");
            System.exit(1);
        }

        //BufferedReader and BufferedWriter automatically using UTF-8 encoding
        try (Socket sock = new Socket(args[0], 7777);
                BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {

            System.out.println("sending message to server " + args[1]);
            out.write(args[1]);
            out.newLine();
            out.flush();

            System.out.println("awaiting response");
            String line = in.readLine();
            System.out.println("message from server: " + line);
        }
    }
}

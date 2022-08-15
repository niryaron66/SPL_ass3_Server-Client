package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;
import bgu.spl.net.impl.BGSServer.User;

import static bgu.spl.net.impl.BGSServer.MessageEncoderDecoderImpl.shortToBytes;


public class Stat extends Message{
    private final short OPCODE = 8;
    private String[] listOfUserNames;

    public Stat(String usernames)
    {
        listOfUserNames=usernames.split("\\|");
    }


    private boolean anyOneInTheListBlockMe(User user,String[] listOfUserNames,DB database)
    {
        for (String tmpUserName : listOfUserNames) {
            int tmpUserNameID = database.getUserName_ConnectionID().get(tmpUserName);
            User tmpUser = database.getRegisterUsers().get(tmpUserName);
            if (user.isBlocked(tmpUser))
                return true;
        }
        return false;
    }
    private boolean anyOneIsRegistered(String[] usernames,DB database)
    {
        for(String username : usernames)
        {
            if(!database.getRegisterUsers().containsKey(username))
                return false;
        }
        return true;
    }

    @Override
    public void process(int connectionId, Connections connections, DB database) {
        User user = database.getLoggedInUser().get(connectionId);
        if (user == null || !user.isLoggedIn()|| !anyOneIsRegistered(listOfUserNames,database) || anyOneInTheListBlockMe(user,listOfUserNames,database) ) {
            Error errorMessage = new Error(OPCODE);
            connections.send(connectionId, errorMessage);
        } else {
            for (String tmpUserName : listOfUserNames) {
                int tmpUserNameID = database.getUserName_ConnectionID().get(tmpUserName);
                User listUser = database.getRegisterUsers().get(tmpUserName);
                if (listUser != null) {
                    short age = listUser.getAge();
                    short numberofPost = listUser.getNumberOfPost();
                    short numberOfFollowers = listUser.getNumberOfFollowers();
                    short numbereOfFollowing = listUser.getNumberOfFollowing();
                    byte[] byteArray = new byte[8];
                    byteArray[0] = shortToBytes(age)[0];
                    byteArray[1] = shortToBytes(age)[1];
                    byteArray[2] = shortToBytes(numberofPost)[0];
                    byteArray[3] = shortToBytes(numberofPost)[1];
                    byteArray[4] = shortToBytes(numberOfFollowers)[0];
                    byteArray[5] = shortToBytes(numberOfFollowers)[1];
                    byteArray[6] = shortToBytes(numbereOfFollowing)[0];
                    byteArray[7] = shortToBytes(numbereOfFollowing)[1];
                    ACK ackMessage = new ACK(OPCODE, byteArray);
//                    for(int i=0;i<byteArray.length;i++)
//                        System.out.print(byteArray[i]+ " ");
                    connections.send(connectionId, ackMessage);
                } else {
                    Error errorMessage = new Error(OPCODE);
                    connections.send(connectionId, errorMessage);
                    return;
                }

            }
        }
    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }
}

package bgu.spl.net.impl.BGSServer.Messages;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.DB;
import bgu.spl.net.impl.BGSServer.User;

import java.util.Vector;

public class Post extends Message {
    private final short OPCODE = 5;
    private String content;
    private Vector<String> additionalUsers;

    public Post(String content) {
        this.content = content;
        additionalUsers = new Vector<>();
    }

    @Override
    public void process(int connectionId, Connections connections, DB database) {
        User user = database.getLoggedInUser().get(connectionId);
        if (user == null ) {
            Error errorMessage = new Error(OPCODE);
            connections.send(connectionId, errorMessage);
        } else {
            ACK ackMessage = new ACK(OPCODE, null);
            connections.send(connectionId, ackMessage);
            user.post();
            Notification notiMessage = new Notification((byte) '1', user.getUsername(), content);
            for (User followerUser : user.getFollowers().values()) {
                database.addMessage(followerUser, content);
                int followerUserID = database.getUserName_ConnectionID().get(followerUser.getUsername());
                if (followerUser.isLoggedIn()) {
                    connections.send(followerUserID, notiMessage);
                } else {
                    followerUser.addUnReadMessage(notiMessage);
                }
            }
            extractUsers(content, database);
            for (String additionalUser : additionalUsers) {
                User additionalTmpUser = database.getRegisterUsers().get(additionalUser);
                int additionalTmpUserID = database.getUserName_ConnectionID().get(additionalUser);
                if (!user.getFollowers().contains(additionalTmpUser)) {
                    database.addMessage(additionalTmpUser, content);
                    if (!user.isBlocked(additionalTmpUser)) {
                        if (additionalTmpUser.isLoggedIn()) {
                            connections.send(additionalTmpUserID, notiMessage);
                        } else {
                            additionalTmpUser.addUnReadMessage(notiMessage);

                        }
                    }
                }
            }

        }

    }

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    public void extractUsers(String tmpContent, DB database) {
        String tmpUserName;
        while (tmpContent.contains("@")) {
            int tmp = tmpContent.indexOf("@");
            if(tmpContent.indexOf(" ",tmp)!=-1) {
                tmpUserName = tmpContent.substring(tmp + 1, tmpContent.indexOf(" ", tmp));
            }
            else
            {
                tmpUserName=tmpContent.substring(tmp+1,tmpContent.length());
            }
            if (database.getUserName_ConnectionID().containsKey(tmpUserName)) {
                  additionalUsers.add(tmpUserName);
            }
            if(tmpContent.indexOf(" ",tmp)!=-1)
                tmpContent = tmpContent.substring(tmpContent.indexOf(" ", tmp)+1);
            else
                break;


    }
} }

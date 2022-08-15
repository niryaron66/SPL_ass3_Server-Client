package bgu.spl.net.impl.BGSServer;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class DB {


    private ConcurrentHashMap<String, User> registerUsers = new ConcurrentHashMap();
    private ConcurrentHashMap<Integer, User> loggedInUser = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Integer> userName_ConnectionID = new ConcurrentHashMap();
    private Vector<String> pmAndPostMessages=new Vector<>();
    private String[] forbiddenWords=new String[]{"Nir","Trump","Maor" } ;

    private static DB database = null;

    public static DB getInstance() {
        if (database == null) {
            database = new DB();
        }
        return database;
    }


    public void registerClient(int connectionId, User user, String username) {
        registerUsers.put(username, user);
        userName_ConnectionID.put(user.getUsername(), connectionId);
        user.register();

    }
    public void addMessage(User user, String message)
    {
        pmAndPostMessages.add(message);
        user.addMessage(message);
    }

    public boolean follow(int userId, String followUserName) {
//        check if the user exist
        User user=loggedInUser.get(userId); // user need to unfollow
        User userToFollow=registerUsers.get(followUserName);
        if(user!=null && user.isLoggedIn() ) {
            if (userToFollow!=null && !user.isBlocked(userToFollow)) {
                //check that he is logged in && already follow him
                if (!user.getFollowing().contains(userToFollow)) {
                    int followID = userName_ConnectionID.get(followUserName);
                    user.addFollowing(followID,userToFollow);
                    userToFollow.addFollower(userId,user);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean unfollow(int userId, String followUserName) {// userId need to unfollow after followuserName
//        check if the user exist
        User user=loggedInUser.get(userId); // user need to unfollow
        User userToUnFollow=registerUsers.get(followUserName);
        if(user!=null && user.isLoggedIn()) {
            if (userToUnFollow!=null) {
                //check that he is logged in && already follow him
                if (user.getFollowing().contains(userToUnFollow)) {
                    int followID = userName_ConnectionID.get(followUserName);
                    user.removeFollowing(followID,userToUnFollow);
                    userToUnFollow.removeFollower(userId,user);
                    return true;
                }
            }
        }
        return false;
    }



    public ConcurrentHashMap<String, User> getRegisterUsers() {
        return registerUsers;
    }

    public String[] getForbiddenWords() {
        return forbiddenWords;
    }

    public ConcurrentHashMap<String, Integer> getUserName_ConnectionID() {
        return userName_ConnectionID;
    }
    
    public boolean isRegistered(int connectionId)
    {
        return registerUsers.containsKey(connectionId);
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public ConcurrentHashMap<Integer, User> getLoggedInUser() {
        return loggedInUser;
    }

    public void addLogInUser(int connectionId, User user) {
        loggedInUser.put(connectionId, user);
    }
}

package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T>  implements Connections<T> {


//    private AtomicInteger idCounter = new AtomicInteger(0);
    private ConcurrentHashMap<Integer , ConnectionHandler<T>> activeClient;
    public ConnectionsImpl()
    {
        activeClient = new ConcurrentHashMap<>();
    }
    @Override
    public boolean send(int connectionId, T msg) {
        if (activeClient.get(connectionId) == null)
            return false;

        activeClient.get(connectionId).send(msg);
     //   System.out.println("");
       // System.out.println("Sending " + msg.getClass() + " client number: " + connectionId);
        return true;
    }


    @Override
    public void broadcast(T msg) {
        System.out.println("Sending BroadCast to all active client");
        for (int connectionId : activeClient.keySet())
            this.send(connectionId , msg);

    }

    @Override
    public void disconnect(int connectionId) {
        if (activeClient.contains(connectionId)) {
            System.out.println("Client - " + connectionId + " disconnected");
            activeClient.remove(connectionId);


        }

    }

    public void addClientConnection (int connectionId , ConnectionHandler<T> ch){
        if (activeClient.contains(connectionId)) {
            System.out.println("Client Already Connected");
        }
        else{
            activeClient.put(connectionId , ch);
        }
    }

    public ConcurrentHashMap<Integer, ConnectionHandler<T>> getActiveClient() {
        return activeClient;
    }

    ///////// Connecction get instance for Non\Blocking connection handler/////////
    private ConnectionsImpl<T> connections = null;
    public  ConnectionsImpl<T> getInstance() {
        if (connections == null) {
            connections = new ConnectionsImpl<T>();
        }
        return connections;
    }
}

1)How to run your code
1.1) Server Reactor :
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="<port> <Num of threads> "
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 7 "
Server TPC:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="<port> "
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777 "

1.2) An example for each message just to avoid miss understandig- we dont care
if you assumed that the message is written in capital letters or if the birthday is
saperated by ‘/’ or ‘.’ in your submission- just let us know what you choose in the
file
REGISTER aaa aaa 11-11-1111
LOGIN aaa aaa 1
LOGOUT
FOLLOW 0 aaa
FOLLOW 1 aaa
PM aaa "some message"
POST "some @user message"
BLOCK aaa

2) where in the code you store the filtered set of word
Class DB:
    private String[] forbiddenWords=new String[]{"Nir","Trump","Maor" } ;
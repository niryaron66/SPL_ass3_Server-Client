
#include "../include/connectionHandler.h"
#include <iostream>
#include <ctime>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_) {}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::atomic<bool> logout_flag{false};
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string &line) {
    return getFrameAscii(line, ';');
}

bool ConnectionHandler::sendLine(std::string &line) {
    return sendFrameAscii(line, ';');
}

bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {

    char* byte= new char[2];
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
        //std::cout << "i before opcode" << std::endl;
        getBytes(&ch, 1);
        byte[0]=ch;
        getBytes(&ch, 1);
        byte[1]=ch;
        //std::cout << "i after opcode" << std::endl;
        short opcode=bytesToShort(byte);
        //std::cout << "my opcode is:"<<opcode << std::endl;
        if(opcode==10||opcode==11){
            getBytes(&ch, 1);
            byte[0]=ch;
            getBytes(&ch, 1);
            byte[1]=ch;
            // std::cout << opcode << std::endl;
            short msgOpCode=bytesToShort(byte);
            //std::cout << subject << std::endl;
            if(opcode==10){
                if(msgOpCode==7||msgOpCode==8) {
                    frame+="ACK "+std::to_string(msgOpCode)+" ";
                    logStatOrStatDecode(frame, msgOpCode);
                    //return true;
                }
                else {
                    frame += "ACK " + std::to_string(msgOpCode);
                    //return true;
                }
            }
            if(opcode==11)
                frame+="ERROR "+std::to_string(msgOpCode);
        }
        if(opcode==9){
            frame+="NOTIFICATION ";
            notificationDecode(frame);
        }
        if(opcode==12)
            frame+="BLOCK ";
        do{
            getBytes(&ch, 1);
          //  frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        delete[] byte;
        return false;
    }
    delete[] byte;
    return true;

}


void ConnectionHandler :: notificationDecode(std::string& msg){
    char ch;
    getBytes(&ch, 1);
    if(ch=='1')
    {
     msg+="Public ";
    }
    else
    {
       msg+="PM " ;
    }
    getBytes(&ch, 1);
    while(ch!='\0')
    {
        msg.append(1,ch);
        getBytes(&ch, 1);
    }
    msg.append(1,' ');
    getBytes(&ch, 1);
    while(ch!='\0')
    {
        msg.append(1,ch);
        getBytes(&ch, 1);
    }


}

void ConnectionHandler::logStatOrStatDecode(std::string& msg,short subject) { //TODO check if need to delete Subject
    char* byte= new char[2];
    char ch;
        getBytes(&ch, 1);
        byte[0]=ch;
        getBytes(&ch, 1);
        byte[1]=ch;
        short age=bytesToShort(byte);
        getBytes(&ch, 1);
        byte[0]=ch;
        getBytes(&ch, 1);
        byte[1]=ch;
        short numPost=bytesToShort(byte);
        getBytes(&ch, 1);
        byte[0]=ch;
        getBytes(&ch, 1);
        byte[1]=ch;
        short numfollowers=bytesToShort(byte);
        getBytes(&ch, 1);
        byte[0]=ch;
        getBytes(&ch, 1);
        byte[1]=ch;
        short numfollowing=bytesToShort(byte);
        msg+=std::to_string(age)+" "+std::to_string(numPost)+" "+std::to_string(numfollowers)+" "+std::to_string(numfollowing);
        delete[] byte;
}


vector<string> ConnectionHandler::split(string &frame,char delimiter) {

    vector<string> arguments;
    size_t pos = 0;
    string token;
    while ((pos = frame.find(delimiter)) != std::string::npos) {
        token = frame.substr(0, pos);
        frame.erase(0, pos + 1);
        arguments.push_back(token);
    }
    arguments.push_back(frame);
    return arguments;


}

void ConnectionHandler::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

short ConnectionHandler::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}


bool ConnectionHandler::sendFrameAscii(const std::string &frame, char delimiter) {

    vector<char> charVec;
    char opByteArray[2];
    string line = frame;
    string postLine=line;
    vector<string> args = split(line,' ');
    string type = args[0];
    if (type == "REGISTER") {
        short OPCODE = 1;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));
        string username = args[1];
        for (char c: username) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');
        string password = args[2];
        for (char c: password) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');
        string birtday = args[3];
        for (char c: birtday) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');

    }
    if (type == "LOGIN") {
        short OPCODE = 2;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));
        string username = args[1];
        for (char c: username) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');
        string password = args[2];
        for (char c: password) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');
        string captcha = args[3];
        charVec.push_back(captcha[0]);
        charVec.push_back('\0');


    }
    if (type == "LOGOUT") {
        short OPCODE = 3;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));

    }
    if (type == "FOLLOW") {
        short OPCODE = 4;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));
        string followOrUn = args[1];
        if (followOrUn == "0") charVec.push_back('0');
        else charVec.push_back('1');
        charVec.push_back('\0');
        string username = args[2];
        for (char c: username) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');


    }
    if (type == "POST") {
        short OPCODE = 5;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));
        string content=postLine.substr(5,postLine.size()-1);
        for(char ch : content)
                charVec.push_back(ch);
        charVec.push_back('\0');


    }
    if (type == "PM") {
        short OPCODE = 6;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));
        string userName = args[1];
        for (char c: userName) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');
        // 2+1+userName.length+1
        string content=postLine.substr(4+userName.length(),postLine.size()-1);
        for(char ch : content)
            charVec.push_back(ch);
        charVec.push_back('\0');
        time_t rawtime;
        struct tm * timeinfo;
        char buffer[17];

        time (&rawtime);
        timeinfo = localtime(&rawtime);

        strftime(buffer,sizeof(buffer),"%d-%m-%Y %H:%M",timeinfo);
        std::string str(buffer);
        for (char c : buffer )
        {
            charVec.push_back(c);
        }

    }

    if(type=="LOGSTAT")
    {
        short OPCODE=7;
        shortToBytes(OPCODE,opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray+1));
    }
    if(type=="STAT")
    {
        short OPCODE=8;
        shortToBytes(OPCODE,opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray+1));
        for(unsigned int i=1;i<args.size();i++)
        {
            for(char ch : args[i])
            {
                charVec.push_back(ch);
            }
            if(i!=args.size()-1)
                charVec.push_back('|');
        }
        charVec.push_back('\0');

    }


    if (type == "BLOCK") {
        short OPCODE = 12;
        shortToBytes(OPCODE, opByteArray);
        charVec.push_back(*opByteArray);
        charVec.push_back(*(opByteArray + 1));
        string username = args[1];
        for (char c: username) {
            charVec.push_back(c);
        }
        charVec.push_back('\0');
    }

    int len = 0;
    char byteArray[charVec.size()];
    for (char &c: charVec) {
        byteArray[len++] = c;
    }

    bool result = sendBytes(byteArray, charVec.size());
    if (!result) return false;
    return sendBytes(&delimiter, 1);
}

// Close down the connection properly.

void ConnectionHandler::close() {
    try {

        socket_.close();

    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}
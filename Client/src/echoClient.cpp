
#include <thread>
#include "../include/connectionHandler.h"

using namespace std;
//valgrind --leak-check=full --show-reachable=yes ./BGSclient 127.0.0.1 7777

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/


int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    string host = argv[1];
    short port = atoi(argv[2]);
    atomic<bool> logout_flag{false};
    ConnectionHandler connectionHandler (host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }



    thread write_thread([&connectionHandler, &logout_flag]() {
        while (1) {
            const short bufsize = 1024;
            char buf[bufsize];
            if(!logout_flag){
                std::cin.getline(buf, bufsize);}
            else{
                break;
            }
            std::string line(buf);
            bool send  = connectionHandler.sendLine(line); //appends '\n' to the message. Therefor we send len+1 bytes.
            if(line == "LOGOUT")   sleep(1);
            if (!send) {
                std::cout << "Write thread shutdown.\n" << std::endl;
                break;
            }
        }
        std::cout << "Write thread shutdown.\n" << std::endl;
    });
    thread read_thread([&connectionHandler, &logout_flag]() {
        while (!logout_flag) {
            std::string answer = "";
            if (!connectionHandler.getLine(answer)) {
                std::cout << "Read thread shutdown.\n" << std::endl;
                break;
            }
            std::cout  << answer <<  std::endl;
            if (answer == "ACK 3") {
                logout_flag = true;
                break;
            }
        }
        std::cout << "Read thread shutdown.\n" << std::endl;
    });
    write_thread.join();
    read_thread.join();


    return 0;
}


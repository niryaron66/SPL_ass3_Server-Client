cmake_minimum_required(VERSION 3.21)
project(Client)

set(CMAKE_CXX_STANDARD 11)

set(Boost_INCLUDE_DIR /cygdrive/c/cygwin64/usr/include)

find_package(Boost REQUIRED COMPONENTS system)

include_directories(Boost_INCLUDE_DIR)

add_executable(Client src/echoClient.cpp src/connectionHandler.cpp include/connectionHandler.h src/writeMessage.cpp include/writeMessage.h src/readMessage.cpp include/readMessage.h)

target_link_libraries(${PROJECT_NAME} ${Boost_LIBRARIES})
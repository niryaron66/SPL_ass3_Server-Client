CXX = g++
CXXFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
CPPFLAGS = -I ./include -MMD -MP
LDFLAGS:=-lboost_system -lboost_thread -lpthread

SRCS = $(wildcard ./src/*.cpp)
OBJS = $(patsubst ./src/%.cpp,./bin/%.o, $(SRCS))
DEPS := $(patsubst %.o,%.d, $(OBJS))

# default target
all: ./bin/BGSclient
# build client
./bin/BGSclient: $(OBJS)
	@echo "Building ..."
	$(CXX) $(OBJS) -o $@ $(LDFLAGS)
	@echo "Finished building"

# build cpp files
./bin/%.o: ./src/%.cpp
	$(CXX) $(CXXFLAGS) $(CPPFLAGS) $< -o $@
	
.PHONY: clean
clean:
	@rm -f ./bin/*

-include $(DEPS)

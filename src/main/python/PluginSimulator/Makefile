
CC=gcc
LIBS=-lavutil -lavformat -lavcodec -lpython3.13 -I /usr/include/python3.13/ -L../build/lib.linux-x86_64-cpython-313/ -lpythonWrapper.cpython-313-x86_64-linux-gnu

CFLAGS=-ggdb -flto
SRC=$(shell find . -name "*.c" )
OBJECTS=$(SRC:.c=.o)


all: pluginsimulator

pluginsimulator:$(OBJECTS)
	$(CC)  $(CFLAGS) -o  $@ $(addprefix ./build/,$(^F)) $(LIBS)

%.o:%.c
	$(CC) $(CFLAGS) -c -o $(addprefix ./build/,$(@F)) $^ $(LIBS)


export LD_LIBRARY_PATH=../build/lib.linux-x86_64-cpython-313/

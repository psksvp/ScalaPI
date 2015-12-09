JNI_INCLUDE=/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include
JNI_LINUX=$(JNI_INCLUDE)/linux
CWD=$(shell pwd)

GenI2CJNI:PiI2C.i
	swig -Wall -java -package psksvp.jni.rpi PiI2C.i
	gcc -shared -I$(JNI_INCLUDE) -I$(JNI_LINUX) PiI2C_wrap.c -o libPiI2C.so

BuildPiI2CJAR:GenI2CJNI	
	mkdir -p psksvp/jni/rpi
	cp *.java psksvp/jni/rpi
	javac psksvp/jni/rpi/*.java
	rm -f psksvp/jni/rpi/*.java
	jar cf PiI2C.jar psksvp/jni/rpi/*.class
	rm -rf psksvp

all:BuildPiI2CJAR
	
clean:
	rm -f PiI2C_wrap.c
	rm -f PiI2C.java
	rm -f PiI2CJNI.java
	rm -f libPiI2C.so
	rm -f PiI2C.jar
		
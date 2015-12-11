JNI_INCLUDE=/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include
JNI_LINUX=$(JNI_INCLUDE)/linux

GenGPIOJNI:PiI2C.i
	swig -java -package psksvp.jni.rpi PiGPIO.i
	gcc -shared -I$(JNI_INCLUDE) -I$(JNI_LINUX) -lwiringPi PiGPIO_wrap.c -olibPiGPIO.so

BuildPiGPIOJAR:GenGPIOJNI	
	mkdir -p psksvp/jni/rpi
	cp *.java psksvp/jni/rpi
	javac psksvp/jni/rpi/*.java
	rm -f psksvp/jni/rpi/*.java
	jar cf PiGPIO.jar psksvp/jni/rpi/*.class
	rm -rf psksvp

all:BuildPiGPIOJAR
	
clean:
	rm -f PiGPIO_wrap.c
	rm -f PiGPIO.java
	rm -f PiGPIOJNI.java
	rm -f libPiGPIO.so
	rm -f PiGPIO.jar
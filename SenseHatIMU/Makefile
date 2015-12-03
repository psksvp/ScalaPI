JNI_INCLUDE=/usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/include
JNI_LINUX=$(JNI_INCLUDE)/linux

PiSensors:PiSensors.h PiSensors.cpp main.cpp
	g++ -Wall PiSensors.cpp main.cpp -lRTIMULib -omain

GenPiSensorsJNI:PiSensors.i PiSensors
	swig -Wall -c++ -java -package psksvp.jni.rpi PiSensors.i

BuildPiSensorsJNI:GenPiSensorsJNI
	g++ -Wall -shared -I$(JNI_INCLUDE) -I$(JNI_LINUX) PiSensors.cpp PiSensors_wrap.cxx -lRTIMULib -olibPiSensors.so
	strip libPiSensors.so

BuildPiSensorsJAR:BuildPiSensorsJNI
	mkdir -p psksvp/jni/rpi
	cp *.java psksvp/jni/rpi
	javac psksvp/jni/rpi/*.java
	rm -f psksvp/jni/rpi/*.java
	jar cf PiSensors.jar psksvp/jni/rpi/*.class
	rm -rf psksvp

RunTest:test.scala BuildPiSensorsJAR
	export LD_LIBRARY_PATH=/home/pi/workspace/rpi
	scala -Djava.library.path=/home/pi/workspace/rpi -classpath PiSensors.jar test.scala

all:BuildPiSensorsJNI BuildPiSensorsJAR

clean:
	rm -f *_wrap.cxx
	rm -f *.java
	rm -f *.o
	rm -f libPiSensors.so
	rm -f main
	rm -f PiSensors.jar

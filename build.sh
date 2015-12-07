#!/bin/sh
cd JNI/SenseHatIMU
make all
cd ../../lib
ln -s ../JNI/SenseHatIMU/PiSensors.jar .
ln -s /opt/pi4j/lib/pi4j-core.jar .
ln -s /opt/pi4j/lib/pi4j-device.jar .
ln -s /opt/pi4j/lib/pi4j-service.jar .
cd ..
cp JNI/SenseHatIMU/*.so .
sbt compile
sbt run

#!/bin/sh
mkdir -p lib
cd JNI/SenseHatIMU
make all
cd ../i2c
make all
cd ../../lib
ln -s ../JNI/i2c/PiI2C.jar .
ln -s ../JNI/i2c/libPiI2C.so .
ln -s ../JNI/SenseHatIMU/PiSensors.jar .
ln -s ../JNI/SenseHatIMU/libPiSensors.so .
cd ..
cp JNI/SenseHatIMU/*.so .
cp JNI/i2c/*.so .
sbt package
mkdir temp
cd temp
unzip ../target/scala-2.11/scalapi_2.11-0.1.jar
unzip -n ../lib/PiSensors.jar
unzip -n ../lib/PiI2C.jar
mkdir native
cd native
cp ../JNI/i2c/libPiI2C.so .
cp ../JNI/SenseHatIMU/libPiSensors.so .
cd ../..
jar cvfm ScalaPi.jar MANIFEST.MF temp/ .



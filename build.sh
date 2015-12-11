#!/bin/sh
#need to check this into a Makefile
mkdir -p lib
cd JNI/SenseHatIMU
make all
cd ../i2c
make all
cd ..GPIO
make all
cd ../../lib
rm -f *
ln -s ../JNI/i2c/PiI2C.jar .
ln -s ../JNI/i2c/libPiI2C.so .
ln -s ../JNI/SenseHatIMU/PiSensors.jar .
ln -s ../JNI/SenseHatIMU/libPiSensors.so .
ln -s ../JNI/GPIO/PiGPIO.jar .
ln -s ../JNI/GPIO/libPiGPIO.so .
cd ..
sbt package
rm -rf temp
rm -f ScalaPi.jar
mkdir -p temp
cd temp
unzip ../target/scala-2.11/scalapi_2.11-0.1.jar
unzip -n ../lib/PiSensors.jar
unzip -n ../lib/PiI2C.jar
mkdir native
cd native
cp ../../JNI/i2c/libPiI2C.so .
cp ../../JNI/SenseHatIMU/libPiSensors.so .
cp ../../JNI/GPIO/libPiGPIO.so .
cd ../
jar cvfm ScalaPi.jar ../MANIFEST.MF .
mv ScalaPi.jar ../.
cd ..
rm -rf temp
echo "ScalaPi.jar"
echo "done .."

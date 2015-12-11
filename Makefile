makeNativeLibs:
	cd ./JNI/SenseHatIMU && $(MAKE) all
	cd ./JNI/i2c && $(MAKE) all
	cd ./JNI/GPIO && $(MAKE) all
	
copyNativeToLib: makeNativeLibs
	cp ./JNI/i2c/PiI2C.jar                ./lib/.
	cp ./JNI/SenseHatIMU/PiSensors.jar    ./lib/.
	cp ./JNI/GPIO/PiGPIO.jar              ./lib/.
	
compileScalaCode: copyNativeToLib
	sbt compile
	sbt package

buildJAR: compileScalaCode
	rm -f ScalaPi.jar
	mkdir -p temp
	rm -f ./temp/*
	mkdir -p ./temp/native
	cp ../../JNI/i2c/libPiI2C.so ./temp/native/.
	cp ../../JNI/SenseHatIMU/libPiSensors.so ./temp/native/.
	cp ../../JNI/GPIO/libPiGPIO.so ./temp/native/.
	unzip ../target/scala-2.11/scalapi_2.11-0.1.jar -d ./temp
	unzip -n ../lib/PiSensors.jar -d ./temp
	unzip -n ../lib/PiI2C.jar	-d ./temp
	unzip -n ../lib/PiGPIO.jar -d ./temp
	jar cvfm ScalaPi.jar MANIFEST.MF ./temp

	
all: buildJAR
	

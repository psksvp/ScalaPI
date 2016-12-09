makeNativeLibs:
	cd ./JNI/SenseHatIMU && $(MAKE) all
	cd ./JNI/i2c && $(MAKE) all
	cd ./JNI/GPIO && $(MAKE) all
	
copyNativeToLib: makeNativeLibs
	mkdir -p lib
	rm -f ./lib/*
	cp ./JNI/i2c/PiI2C.jar                ./lib/PiI2C.jar
	cp ./JNI/SenseHatIMU/PiSensors.jar    ./lib/PiSensors.jar
	cp ./JNI/GPIO/PiGPIO.jar              ./lib/PiGPIO.jar
	
compileScalaCode: copyNativeToLib
	sbt compile
	sbt package

buildJAR: compileScalaCode
	rm -f ScalaPi.jar
	mkdir -p temp
	rm -rf ./temp/*
	mkdir -p ./temp/native
	cp ./JNI/i2c/libPiI2C.so ./temp/native/libPiI2C.so
	cp ./JNI/SenseHatIMU/libPiSensors.so ./temp/native/libPiSensors.so
	cp ./JNI/GPIO/libPiGPIO.so ./temp/native/libPiGPIO.so
	unzip ./target/scala-2.12/scalapi_2.12-0.1.jar -d ./temp
	unzip -n ./lib/PiSensors.jar -d ./temp
	unzip -n ./lib/PiI2C.jar -d ./temp
	unzip -n ./lib/PiGPIO.jar -d ./temp
	cd temp && jar cvfm ScalaPi.jar ../MANIFEST.MF . 
	cp ./temp/ScalaPi.jar ScalaPi.jar
	rm -rf temp

	
all: buildJAR
	

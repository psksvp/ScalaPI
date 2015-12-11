makeNativeLibs:
	make -f -C ./JNI/SenseHatIMU/make all
	make -f -C ./JNI/i2c/make all
	make -f -C ./JNI/GPIO/make all
	
makeSymLink: makeNativeLibs
	ln -s ./JNI/i2c/PiI2C.jar                ./lib/.
	ln -s ./JNI/SenseHatIMU/PiSensors.jar    ./lib/.
	ln -s ./JNI/GPIO/PiGPIO.jar              ./lib/.
	
compileScalaCode: makeSymLink
	sbt compile
	sbt package
	
buildJAR: compileScalaCode
	rm -f ScalaPi.jar
	mkdir -p temp
	cd temp
	rm -f *
	unzip ../target/scala-2.11/scalapi_2.11-0.1.jar
	unzip -n ../lib/PiSensors.jar
	unzip -n ../lib/PiI2C.jar
	unzip -n ../lib/PiGPIO.jar
	mkdir native
	cd native
	cp ../../JNI/i2c/libPiI2C.so .
	cp ../../JNI/SenseHatIMU/libPiSensors.so .
	cp ../../JNI/GPIO/libPiGPIO.so .
	cd ../
	jar cvfm ScalaPi.jar ../MANIFEST.MF .
	mv ScalaPi.jar ../.
	

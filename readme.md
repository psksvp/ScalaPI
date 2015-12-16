# ScalaPI: Interfacing Rasberry PI hardware with Scala#

Scala classes to control the 
* GPIO
* i2c
* SenseHAT  https://www.raspberrypi.org/products/sense-hat/
* Adafruit PWM HAT https://learn.adafruit.com/adafruit-16-channel-pwm-servo-hat-for-raspberry-pi/overview
* Adafruit Stepper/DC Motor HAT https://learn.adafruit.com/adafruit-dc-and-stepper-motor-hat-for-raspberry-pi/overview
 
## Requirements
* Scala 2.11.7 or > ("http://downloads.typesafe.com/scala/2.11.7/scala-2.11.7.deb?_ga=1.154127736.1386774362.1448663611")
* sbt (https://dl.bintray.com/sbt/debian/sbt-0.13.9.deb)
* SenseHAT IMU lib. RASPBIAN JESSIE has this by default.
* Swig 3.xx to build (http://www.swig.org)
* JDK 1.8 RASPBIAN JESSIE has this by default.

## Build
After cloning this repos, run make all from command line. A prebuilt jar (ScalaPi.jar) is also provided in the repos.
```
> cd ScalaPi
> make all
```
If all goes well, it will create ScalaPi.jar with all the native libs packed in there. The example below shows how ScalaPi.jar can be used in REPL for a quick test.


## REPL example
```
pi@raspberrypi ~/workspace/rpi/ScalaPI $ scala -classpath ScalaPi.jar
Welcome to Scala version 2.11.7 (Java HotSpot(TM) Client VM, Java 1.8.0).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import psksvp.RPi._
import psksvp.RPi._

scala> val display = SenseHAT.display
display: psksvp.RPi.SenseHAT.LEDDisplay = psksvp.RPi.SenseHAT$LEDDisplay@1615099

scala> display.drawString("HelloWorld", (0, 0, 255))
for(a <- List(0, 90, 180, 270)){ 
     |   display.setRotation(a)
     |   Thread.sleep(1000)
     | }

scala> PWMDevice.using
register device -> psksvp.RPi.Servo
register device -> psksvp.RPi.ESC
register device -> psksvp.RPi.DCMotor
register device -> psksvp.RPi.StepperMotor

scala> val motorHAT = new MotorHAT
motorHAT: psksvp.RPi.MotorHAT = psksvp.RPi.MotorHAT@6de728

scala> val dc0 = motorHAT.attachDevice[DCMotor](channel=1)
DCMotor.init has been called, 13 12 11
dc0: psksvp.RPi.DCMotor = DCMotor()

scala> dc0.setSpeed(30)

scala> dc0.forward

scala> dc0.setSpeed(50)

scala> dc0.setSpeed(20)

scala> dc0.backward

scala> dc0.release

scala> val pwmHAT = new PWMHAT
pwmHAT: psksvp.RPi.PWMHAT = psksvp.RPi.PWMHAT@3fc542

scala> val servo0 = pwmHAT.attachDevice[Servo](channel=0)

scala> dc0.forward

scala> servo0.set(90)

scala> servo0.set(45)

scala> dc0.release

scala> servo0.set(20)

```

## Sample code
There is no documentation yet, please look at the example code below. 

~~~scala
/**
  * Created by psksvp on 30/11/2015.
  */
object RPiMain
{
  def main(args:Array[String]):Unit=
  {
    //testSenseHatStick
    //testSenseHatDisplayRotate
    //testSenseHatDisplayRandomColors
    //testSenseHatSensors
    //testPWMHatServo
    testMotorHAT
    //testSenseHatDisplyChar
  }

  def testSenseHatDisplyChar:Unit=
  {
    println("test display char")
    import psksvp.Symbols.AsciiBitmap
    import scala.util.Random
    import psksvp.RPi.SenseHAT
    val rdGen = new Random
    val display = SenseHAT.display
    display.drawString("psksvp@gmail.com", (0, 0, 255))
    for(i <- 0 to 255)
    {
      val r = rdGen.nextInt(256)
      val g = rdGen.nextInt(256)
      val b = rdGen.nextInt(256)
      display.clear
      display.drawBitmap(AsciiBitmap(i), (r,g,b))
      display.update
      Thread.sleep(150)
    }
  }

  def testSenseHatDisplayRotate:Unit=
  {
    import psksvp.RPi.SenseHAT
    val display = SenseHAT.display
    val index = (0 to 7)
    display.clear
    for(i <- index)
    {
      display.setPixel(3, i, (255, 0, 0))
      display.setPixel(4, i, (0, 0, 255))
      display.setPixel(i, 0, (0, 255, 0))
    }

    display.update
    var cnt = 0
    while(cnt < 2)
    {
      for(r <- List(0, 90, 180, 270))
      {
        println("display rotation is " + r)
        display.setRotation(r)
        Thread.sleep(1000)
      }
      cnt = cnt + 1
    }
    display.clear
  }

  def testSenseHatDisplayRandomColors:Unit=
  {
    println("testDisplayRandomColors")
    import scala.util.Random
    import psksvp.RPi.SenseHAT
    val rdGen = new Random
    val display = SenseHAT.display
    val index = (0 to 7)
    var cnt = 0
    while(cnt < 5)
    {
      for (x <- index; y <- index)
      {
        val r = rdGen.nextInt(256)
        val g = rdGen.nextInt(256)
        val b = rdGen.nextInt(256)
        display.setPixel(x, y, (r, g, b))
      }
      display.update
      Thread.sleep(1000)
      cnt = cnt + 1
    }
    display.clear
  }

  def testSenseHatSensors:Unit=
  {
    import psksvp.RPi.SenseHAT
    val sensors = SenseHAT.sensors
    var m = 10
    while(m > 0)
    {
      sensors.poll match
      {
        case Some(data) => println("humidity     -> " + data.environment.humidity)
                           println("pressure     -> " + data.environment.pressure)
                           println("height       -> " + data.environment.height)
                           println("temperature  -> " + data.environment.temperature)

                           println("pos roll     -> " + data.pose.roll)
                           println("pos pitch    -> " + data.pose.pitch)
                           println("pos yaw      -> " + data.pose.yaw)

                           println("gyro roll    -> " + data.gyro.roll)
                           println("gyro pitch   -> " + data.gyro.pitch)
                           println("gyro yaw     -> " + data.gyro.yaw)

                           println("accel roll   -> " + data.accelerometer.roll)
                           println("accel pitch  -> " + data.accelerometer.pitch)
                           println("accel yaw    -> " + data.accelerometer.yaw)

                           println("compass roll -> " + data.compass.roll)
                           println("compass pitch-> " + data.compass.pitch)
                           println("compass yaw  -> " + data.compass.yaw)
                           println("====================================================")
        case None       => println("sensors poll fail")
      }
      Thread.sleep(1000)
      m = m - 1
    }

    sensors.deinit
  }

  def testSenseHatStick:Unit=
  {
    import psksvp.RPi.SenseHAT
    val display = SenseHAT.display
    val index = (0 to 7)
    display.clear
    display.drawBitmap(0x18182424427E4242L, (255, 0, 0))
    display.update

    println("test sense stick\nmove the stick to test\npush down to exit")
    val stick = SenseHAT.stick
    var code = stick.read
    while(SenseHAT.kENTER != code)
    {
      println(code)
      code match
      {
        case SenseHAT.kUP    => display.setRotation(0)
        case SenseHAT.kLEFT  => display.setRotation(270)
        case SenseHAT.kRIGHT => display.setRotation(90)
        case SenseHAT.kDOWN  => display.setRotation(180)
        case _               =>
      }

      code = stick.read
    }
  }

  def testPWMHatServo:Unit=
  {
    println("TestServo PWM")
    import psksvp.RPi.{Servo, PWMHAT}
    PWMDevice.using
    val pwmHat = new PWMHAT
    val servo0 = pwmHat.attachDevice[Servo](channel=0)
    val servo1 = pwmHat.attachDevice[Servo](channel=15)

    while(true)
    {
      print("enter an angle:")
      val angle = scala.io.StdIn.readInt()
      if(angle < 0)
        return
      else
      {
        servo0.set(angle)
        servo1.set(angle)
      }
    }
  }

  def testMotorHAT:Unit=
  {
    println("test Motor HAT")
    import psksvp.RPi._
    PWMDevice.using

    val motorHAT = new MotorHAT
    val dc1 = motorHAT.attachDevice[DCMotor](channel = 1)
    var speed = 10
    while(0 != speed)
    {
      print("enter speed:")
      speed = scala.io.StdIn.readInt()
      dc1.setSpeed(Math.abs(speed))
      if(speed > 0)
      {
        println("forward at speed " + speed)
        dc1.forward
      }
      else if(speed < 0)
      {
        println("backward at speed " + speed)
        dc1.backward
      }
    }
    println("release the motor")
    dc1.release
  }

  def testGPIO:Unit=
  {
    import psksvp.RPi.GPIO
    val outPin = GPIO.getOutputPin(0)
    outPin.digitalWrite(GPIO.HIGH())
  }
}

~~~
## License
 The BSD 3-Clause License
 Copyright (c) 2015, Pongsak Suvanpong (psksvp@gmail.com)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
## Acknowledgement
* Use code from https://github.com/adamheinrich/native-utils for loading shared lib from jar.
* The native libs use wiringPi (http://wiringpi.com) and RTIMUlib (https://github.com/richards-tech/RTIMULib)
* PWMHAT and MotorHAT classes were written by looking at python code from Adafruit (https://github.com/adafruit).
 
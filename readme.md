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

## REPL example
```
pi@raspberrypi ~/workspace/rpi/ScalaPI $ scala -classpath ScalaPi.jar
Welcome to Scala version 2.11.7 (Java HotSpot(TM) Client VM, Java 1.8.0).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import psksvp.RPi.SenseHAT
import psksvp.RPi.SenseHAT

scala> val display = SenseHAT.display
display: psksvp.RPi.SenseHAT.LEDDisplay = psksvp.RPi.SenseHAT$LEDDisplay@1615099

scala> display.drawString("HelloWorld", (0, 0, 255))
for(a <- List(0, 90, 180, 270)){ 
     |   display.setRotation(a)
     |   Thread.sleep(1000)
     | }
scala> import psksvp.RPi.{Servo, PWMHAT}
import psksvp.RPi.{Servo, PWMHAT}

scala> val pwmHat = new PWMHAT
pwmHat: psksvp.RPi.PWMHAT = psksvp.RPi.PWMHAT@9d82f9

scala> val servo0 = Servo(armAngleRange = (0 to 180))

scala> pwmHat.attachDevice(servo0, channel=0)

scala> servo0.set(90)

scala> servo0.set(180)

scala> servo0.set(0)

scala> servo0.set(45)
```

## Sample code
There is no documentation yet, please look at the example code below. 



```
object RPiMain
{
  def main(args:Array[String]):Unit=
  {
    testSenseHatStick
    testSenseHatDisplayRotate
    testSenseHatDisplayRandomColors
    testSenseHatSensors
    testSenseHatDisplyChar
    testPWMHatServo
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
      Thread.sleep(1000)
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
    val servo1 = Servo(armAngleRange = (0 to 180))
    val servo0 = Servo(armAngleRange = (0 to 180))
    val pwmHat = new PWMHAT
    pwmHat.attachDevice(servo1, channel=0)
    pwmHat.attachDevice(servo0, channel=3)

    while(true)
    {
      print("enter an angle:")
      val angle = scala.io.StdIn.readInt()
      servo0.set(angle)
      servo1.set(angle)
    }
  }
}
```
## Contact 
pongsak suvanpong (psksvp@gmail.com)
## Acknowledgement
Use code from https://github.com/adamheinrich/native-utils

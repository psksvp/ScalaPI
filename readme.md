# ScalaPI: Interfacing with Rasberry PI hardware with Scala#

I hate snake, but I like Scala and want to use this for my Robotic boat. 
Bah bah bah ... if you are a scala programmer, the example code below will make more sense than bah bah bah

currently it requires pi4j (https://github.com/Pi4J/pi4j) for i2c, but I will eventually remove, becasue of incompatible license.

TODO:  include sbt buit script.


```
object RPiMain
{
  def main(args:Array[String]):Unit=
  {
    testSenseHatDisplayRotate
    testSenseHatDisplayRandomColors
    testSenseHatSensors
    testPWMHatServo
  }

  def testSenseHatDisplayRotate:Unit=
  {
    import psksvp.RPi.SenseHat
    val display = SenseHat.display
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
    import psksvp.RPi.SenseHat
    val rdGen = new Random
    val display = SenseHat.display
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
    import psksvp.RPi.SenseHat
    val sensors = SenseHat.sensors
    var m = 10
    while(m > 0)
    {
      sensors.poll match
      {
        case Some(data) =>
          /*
          println("----Environment------")
          println(data.environment)
          println("----IMU gyro--------------")
          println(data.gyro)
          println("----IMU accelerometer-----")
          println(data.accelerometer)
          println("----IMU pose-----")
          println(data.pose)
          println("----IMU compass-----")
          println(data.compass) */
          println("====================================================")
          println("humidity     -> " + data.environment.humidity)
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
        case None       => println("sensors poll fail")
      }
      Thread.sleep(1000)
      m = m - 1
    }

    sensors.deinit
  }

  def testPWMHatServo:Unit=
  {
    println("TestServo PWM")
    import psksvp.RPi.{Servo, PWMHat}
    val servo1 = Servo(armAngleRange = (0 to 180))
    val servo0 = Servo(armAngleRange = (0 to 180))
    val pwmHat = new PWMHat
    pwmHat.attachDevice(servo1, channel=0)
    pwmHat.attachDevice(servo0, channel=3)
    var pos = 0
    while(pos <= 180)
    {
      println("Angle -> " + pos)
      servo1.set(pos)
      servo0.set(pos)
      Thread.sleep(1000)
      pos = pos + 1
    }
  }
}

```

/**
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
  **/
package psksvp.Main

/**
  * Created by psksvp on 30/11/2015.
  */
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

/**
 PiSensors JVM jni module
 jni interface for Raspberry pi sense hat's IMU sensors

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
///////////////////////////////////////////////////////////////
object EntryPoint
{
  /*
    SensorDevice wraps the uglyness of java getBah.. 
  */
  class SensorDevice
  {
    import psksvp.jni.rpi.{PiSensors, SensorData}
    class SensorValues(data:SensorData)
    {
      case class Environment(humidity:Double, pressure:Double, height:Double, temperature:Double)
      case class Orientation(roll:Double, pitch:Double, yaw:Double)

      def valid=data.getValid
      def environment = Environment(data.getHumidity, data.getPressure,data.getHeight, data.getTemperature)
      def gyro = Orientation(data.getGyro.getRoll, data.getGyro.getPitch, data.getGyro.getYaw)
      def accelerometer = Orientation(data.getAccel.getRoll, data.getAccel.getPitch, data.getAccel.getYaw)
      def pose = Orientation(data.getPose.getRoll, data.getPose.getPitch, data.getPose.getYaw)
      def compass = Orientation(data.getCompass.getRoll, data.getCompass.getPitch, data.getCompass.getYaw)
    }

    def init:Boolean=
    {
      try
      {
        System.loadLibrary("PiSensors")
        PiSensors.start()
      }
      catch
      {
        case e:UnsatisfiedLinkError => println("Native code library failed to load.\n" + e)
                                       false
      }
    } // class SensorDevice

    def deinit:Unit=PiSensors.stop()
    def poll:Option[SensorValues]=
    {
      val data = new SensorValues(PiSensors.poll())
      if(data.valid)
        Some(data)
      else
      {
        println("SenseHat.pollSensors data are not valid")
        None
      }
    }
  }
  
  //////////////////////////////////////////////
  ////
  def main(args:Array[String]):Unit=
  {
    try 
    {
      //val cwd = new java.io.File(".")
      //System.setProperty("java.library.path", cwd.getCanonicalPath)
      //println("setting java.library.path to " + cwd.getCanonicalPath)
      System.loadLibrary("PiSensors")
      println("loaded PiSensors.so")
    } 
    catch
    {
      case e:UnsatisfiedLinkError =>
        System.err.println("Native code library failed to load.\n" + e)
        System.exit(1)
    } 
    
    val sensorDevice = new SensorDevice
    if(true == sensorDevice.init)
    {
      var cnt = 100
      while(cnt > 0)
      {
        sensorDevice.poll match
        {
          case Some(data) =>
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
        } //match
        cnt = cnt - 1
      } //while
      
      sensorDevice.deinit
    } 
  }
}




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

object testRpiSensors
{
  def main(args:Array[String]):Unit=
  {
    try 
    {
      //val cwd = new java.io.File(".")
      //System.setProperty("java.library.path", cwd.getCanonicalPath)
      //println("setting java.library.path to " + cwd.getCanonicalPath)
      System.loadLibrary("PiSensors")
    } 
    catch
    {
      case e:UnsatisfiedLinkError =>
        System.err.println("Native code library failed to load.\n" + e)
        System.exit(1)
    } 
    
    import psksvp.jni.rpi._
    PiSensors.start()
    var cnt = 100
    while(cnt > 0)
    {
      val data = PiSensors.poll
      if(true == data.getValid)
      {
        println("humidity     -> " + data.getHumidity)
        println("pressure     -> " + data.getPressure)
        println("height       -> " + data.getHeight)
        println("temperature  -> " + data.getTemperature)
        
        println("pos roll     -> " + data.getPose.getRoll)
        println("pos pitch    -> " + data.getPose.getPitch)
        println("pos yaw      -> " + data.getPose.getYaw)
        
        println("gyro roll    -> " + data.getGyro.getRoll)
        println("gyro pitch   -> " + data.getGyro.getPitch)
        println("gyro yaw     -> " + data.getGyro.getYaw)
        
        println("accel roll   -> " + data.getAccel.getRoll)
        println("accel pitch  -> " + data.getAccel.getPitch)
        println("accel yaw    -> " + data.getAccel.getYaw)
        
        println("compass roll -> " + data.getCompass.getRoll)
        println("compass pitch-> " + data.getCompass.getPitch)
        println("compass yaw  -> " + data.getCompass.getYaw)
        println("============================================")
      }
      cnt = cnt - 1
    }
    
    PiSensors.stop()
  }
}
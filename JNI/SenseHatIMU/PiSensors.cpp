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

#include <RTIMULib.h>
#include <cstdio>
#include <cstring>
#include "PiSensors.h"

#include <unistd.h>
#include <sys/types.h>
#include <pwd.h>



namespace PiSensors
{
  RTIMUSettings* mySetting = NULL;
  RTHumidity* myHumidity = NULL;
  RTPressure* myPressure = NULL;
  RTIMU* myIMU = NULL;

  void stop()
  {
    if(NULL != mySetting)
      delete mySetting;
    if(NULL != myHumidity)
      delete myHumidity;
    if(NULL != myPressure)
      delete myPressure;
    if(NULL != myIMU)
      delete myIMU;
  }

  bool start()
  {
    if(NULL == mySetting)
    {
      const char *homedir;
      
      if ((homedir = getenv("HOME")) == NULL)
      {
        homedir = getpwuid(getuid())->pw_dir;
      }
      
      String strINIPath = homedir + "/.config/RTIMULib";
      
      
      mySetting = new RTIMUSettings(strINIPath.c_str());
      
      myIMU = RTIMU::createIMU(mySetting);
      if((myIMU == NULL) || (myIMU->IMUType() == RTIMU_TYPE_NULL))
      {
        std::fprintf(stderr, "PiSensor::start, no IMU found");
        if(NULL != myIMU)
        {
          delete myIMU;
          myIMU = NULL;
        }
        return false;
      }
      else
      {
        myIMU->IMUInit();
        myIMU->setSlerpPower(0.02);
        myIMU->setGyroEnable(true);
        myIMU->setAccelEnable(true);
        myIMU->setCompassEnable(true);
      }
      
      myHumidity = RTHumidity::createHumidity(mySetting);
      if(false == myHumidity->humidityInit())
      {
        std::fprintf(stderr, "PiSensor::start, Fail to init Humidity sensor");
        delete myHumidity;
        myHumidity = NULL;
        return false;
      }
      myPressure = RTPressure::createPressure(mySetting);
      if(false == myPressure->pressureInit())
      {
        std::fprintf(stderr, "PiSensor::start, Fail to init Pressure sensor");
        delete myPressure;
        myPressure = NULL;
        return false;
      }
    }
    return true;
  }
  
  SensorData poll()
  {
    usleep(myIMU->IMUGetPollInterval() * 1000);
    
    SensorData env;
    env.valid = true;
    if(myIMU->IMURead())
    {
      RTIMU_DATA data = myIMU->getIMUData();
      
      env.pose.roll = data.fusionPose.x() * RTMATH_RAD_TO_DEGREE;
      env.pose.pitch = data.fusionPose.y() * RTMATH_RAD_TO_DEGREE;
      env.pose.yaw = data.fusionPose.z() * RTMATH_RAD_TO_DEGREE;
      
      env.gyro.roll = data.gyro.x() * RTMATH_RAD_TO_DEGREE;
      env.gyro.pitch = data.gyro.y() * RTMATH_RAD_TO_DEGREE;
      env.gyro.yaw = data.gyro.z() * RTMATH_RAD_TO_DEGREE;
      
      env.accel.roll = data.accel.x() * RTMATH_RAD_TO_DEGREE;
      env.accel.pitch = data.accel.y() * RTMATH_RAD_TO_DEGREE;
      env.accel.yaw = data.accel.z() * RTMATH_RAD_TO_DEGREE;
      
      env.compass.roll = data.compass.x() * RTMATH_RAD_TO_DEGREE;
      env.compass.pitch = data.compass.y() * RTMATH_RAD_TO_DEGREE;
      env.compass.yaw = data.compass.z() * RTMATH_RAD_TO_DEGREE;
    
    
      if(NULL != myHumidity)
      {
        myHumidity->humidityRead(data);
        env.humidity = data.humidity;
      }
      else
      {
        std::fprintf(stderr, "humidity is NULL");
        env.humidity = 0.0;
      }
    
      if(NULL != myPressure)
      {
        myPressure->pressureRead(data);
        env.pressure = data.humidity;
        env.temperature = data.temperature;
        env.height = RTMath::convertPressureToHeight(data.pressure);
      }
      else
      {
        std::fprintf(stderr, "pressure is NULL");
        env.pressure = 0.0;
        env.temperature = 0.0;
      }
    }
    else
    {
      std::fprintf(stderr, "PiSensors::poll sensors read fail");
      env.valid = false;
    }
    return env;
  }
}


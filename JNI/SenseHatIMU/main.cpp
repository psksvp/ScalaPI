#include "PiSensors.h"
#include <cstdio>

int main(int argc, char** argv)
{
  PiSensors::start();
  for(int i = 0; i < 100; i++)
  {
    PiSensors::SensorData env;
    env = PiSensors::poll();
    if(true == env.valid)
    {
      std::printf("humidity:%f, pressure:%f, temperature:%f, height:%f\n",
                  env.humidity, env.pressure, env.temperature, env.height);
    
      std::printf("pose roll:%f, pose pitch:%f, pose yaw:%f\n",
                  env.pose.roll, env.pose.pitch, env.pose.roll);
      
      std::printf("gyro roll:%f, gyro pitch:%f, gyro yaw:%f\n",
                  env.gyro.roll, env.gyro.pitch, env.gyro.roll);
      
      
      std::printf("accel roll:%f, accel pitch:%f, accel yaw:%f\n",
                  env.accel.roll, env.accel.pitch, env.accel.roll);
      
      std::printf("compass roll:%f, compass pitch:%f, compass yaw:%f\n",
                  env.compass.roll, env.compass.pitch, env.compass.roll);
      std::printf("_______________________________________________________\n");
      
    }
    
  }
  
  PiSensors::stop();
  return 0;
}
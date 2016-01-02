#ifndef __PI_CAMERA__
#define __PI_CAMERA__


namespace PiCamera
{
  bool start(int width, int height);
  void stop();
  
  unsigned char* grab();
}


#endif
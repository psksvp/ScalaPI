package psksvp

/**
  * Created by psksvp on 12/01/2016.
  */
package object RPi
{
  def version:Int=
  {
    import psksvp.FileSystem.SimpleFileIO
    val cpuinfo = SimpleFileIO.readTextFromFile("/proc/cpuinfo")
    if(cpuinfo.indexOf("BCM2709") >= 0)
      2
    else
      1
  }
}

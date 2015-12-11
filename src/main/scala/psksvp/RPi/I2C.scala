package psksvp.RPi

/**
  * Created by psksvp on 11/12/2015.
  */
class I2C(endPointAddress:Int)
{
  import psksvp.jni.rpi.PiI2C
  val fd = PiI2C.wiringPiI2CSetup(endPointAddress)

  def read=PiI2C.wiringPiI2CRead(fd).toByte
  def write(data:Byte)=PiI2C.wiringPiI2CWrite(fd, data.toInt)

  def read(reg:Int)=PiI2C.wiringPiI2CReadReg8(fd, reg).toByte
  def write(reg:Int, data:Byte)=PiI2C.wiringPiI2CWriteReg8(fd, reg, data.toInt)
}

object I2C
{
  private var init=false
  try
  {
    if(false == init)
    {
      //val cwd = (new java.io.File(".")).getCanonicalPath
      //psksvp.FileSystem.SimpleFileIO.setLibraryPath(cwd)
      //System.loadLibrary("PiI2C")
      import psksvp.FileSystem.SimpleFileIO
      SimpleFileIO.loadNativeLibraryFromJar("/native/libPiI2C.so")
      init = true
    }
  }
  catch
  {
    case e:UnsatisfiedLinkError => sys.error("Native code library failed to load.\n" + e)
  }

  def connect(endPointAddress:Int) = new I2C(endPointAddress)
}

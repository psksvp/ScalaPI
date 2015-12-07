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

package psksvp.RPi

/**
  * Created by psksvp on 29/11/2015.
  *
  * this class interfaces with Sense Hat
  * https://www.raspberrypi.org/products/sense-hat/
  *
  * There can be only one SenseHat attached with Raspberry Pi
  * thus, SenseHat is Object
  */
object SenseHAT
{
  //////////////////// 8x8 LED ///////////////////////
  /**
    *
    * @param fbDevicePath
    */
  class LEDDisplay(fbDevicePath:String)
  {
    private var rotation = 0
    // this lookup tables are from SenseHat snake library.
    private val pixMap000 = Array(Array( 0,  1,  2,  3,  4,  5,  6,  7),
                                  Array( 8,  9, 10, 11, 12, 13, 14, 15),
                                  Array(16, 17, 18, 19, 20, 21, 22, 23),
                                  Array(24, 25, 26, 27, 28, 29, 30, 31),
                                  Array(32, 33, 34, 35, 36, 37, 38, 39),
                                  Array(40, 41, 42, 43, 44, 45, 46, 47),
                                  Array(48, 49, 50, 51, 52, 53, 54, 55),
                                  Array(56, 57, 58, 59, 60, 61, 62, 63))
    private val pixMap090 = Array(Array( 7, 15, 23, 31, 39, 47, 55, 63),
                                  Array( 6, 14, 22, 30, 38, 46, 54, 62),
                                  Array( 5, 13, 21, 29, 37, 45, 53, 61),
                                  Array( 4, 12, 20, 28, 36, 44, 52, 60),
                                  Array( 3, 11, 19, 27, 35, 43, 51, 59),
                                  Array( 2, 10, 18, 26, 34, 42, 50, 58),
                                  Array( 1,  9, 17, 25, 33, 41, 49, 57),
                                  Array( 0,  8, 16, 24, 32, 40, 48, 56))
    private val pixMap180 = Array(Array(63, 62, 61, 60, 59, 58, 57, 56),
                                  Array(55, 54, 53, 52, 51, 50, 49, 48),
                                  Array(47, 46, 45, 44, 43, 42, 41, 40),
                                  Array(39, 38, 37, 36, 35, 34, 33, 32),
                                  Array(31, 30, 29, 28, 27, 26, 25, 24),
                                  Array(23, 22, 21, 20, 19, 18, 17, 16),
                                  Array(15, 14, 13, 12, 11, 10,  9,  8),
                                  Array( 7,  6,  5,  4,  3,  2,  1,  0))
    private val pixMap270 = Array(Array(56, 48, 40, 32, 24, 16,  8,  0),
                                  Array(57, 49, 41, 33, 25, 17,  9,  1),
                                  Array(58, 50, 42, 34, 26, 18, 10,  2),
                                  Array(59, 51, 43, 35, 27, 19, 11,  3),
                                  Array(60, 52, 44, 36, 28, 20, 12,  4),
                                  Array(61, 53, 45, 37, 29, 21, 13,  5),
                                  Array(62, 54, 46, 38, 30, 22, 14,  6),
                                  Array(63, 55, 47, 39, 31, 23, 15,  7))

    val pixMap = Map(0->pixMap000, 90->pixMap090, 180->pixMap180, 270->pixMap270)
    val pixBuf = Array.ofDim[Byte](128) //(2 * 8 * 8) // 8x8 2 byte each


    def update:Unit=
    {
      import java.io.FileOutputStream

      val f = new FileOutputStream(fbDevicePath)
      f.write(pixBuf)
      f.flush()
      f.close()
    }

    def clear:Unit=
    {
      for(i <- pixBuf.indices)
        pixBuf(i) = 0
      update
    }

    def setRotation(r:Int): Unit =
    {
      require(pixMap.contains(r))
      val index = (0 to 7)
      val pixBufTemp = Array.ofDim[(Int, Int, Int)](8, 8)
      for(x <- index; y <- index)
        pixBufTemp(x)(y) = getPixel(x, y)
      rotation = r
      for(x <- index; y <- index)
        setPixel(x, y, pixBufTemp(x)(y))

      update
    }

    def setPixel(x:Int, y:Int, color:(Int, Int, Int)): Unit =
    {
      def packRGB(red:Int, green:Int, blue:Int):Array[Byte]=
      {
        val r = (red >> 3) & 0x1F
        val g = (green >> 2) & 0x3F
        val b = (blue >> 3) & 0x1F
        val pix16 = (r << 11) + (g << 5) + b
        Array[Byte]((pix16 & 0xff).toByte, ((pix16 >> 8) & 0xff).toByte)
      }

      require(x >= 0 && x < 8)
      require(y >= 0 && y < 8)
      val map = pixMap(rotation)
      val offset = map(y)(x) * 2
      val pix = packRGB(color._1, color._2, color._3)
      pixBuf(offset) = pix(0)
      pixBuf(offset + 1) = pix(1)
    }

    def getPixel(x:Int, y:Int):(Int, Int, Int)=
    {
      def unpackRGB(pix16:Int):(Int, Int, Int)=
      {
        val r = (pix16 & 0xF800) >> 11
        val g = (pix16 & 0x7E0) >> 5
        val b = pix16 & 0x1F
        (r << 3, g << 2, b << 3)
      }

      require(x >= 0 && x < 8)
      require(y >= 0 && y < 8)
      val map = pixMap(rotation)
      val offset = map(y)(x) * 2
      val low = pixBuf(offset)
      val high = pixBuf(offset + 1)
      unpackRGB( (((high & 0xFF) << 8) | (low & 0xFF)).toShort )
    }

    def drawBitmap(bits:Long, color:(Int, Int, Int)):Unit=
    {
      import java.lang.Long
      val bin = ("0" * Long.numberOfLeadingZeros(bits)) + bits.toBinaryString
      drawBitmap(bin, color)
    }

    def drawBitmap(bits:String, color:(Int, Int, Int), offset:(Int, Int)=(0,0)):Unit=
    {
      require(64 == bits.length)
      val index = 0 to 7
      var i = 0
      for (y <- index; x <- index)
      {
        if ('1' == bits(i))
        {
          val dx = x + offset._1
          val dy = y + offset._2
          if(dx >= 0 && dx < 8 && dy >= 0 && dy < 8)
            setPixel(dx, dy, color)
        }
        i = i + 1
      }
    }

    def drawChar(c:Char, color:(Int, Int, Int), slide:Int=0):Unit=
    {
      import psksvp.Symbols.AsciiBitmap
      if(0 == slide)
      {
        clear
        drawBitmap(AsciiBitmap(c.toInt), color)
        update
      }
      else
      {
        for (sx <- (0 to 7))
        {
          clear
          drawBitmap(AsciiBitmap(c.toInt), color, offset = (-sx, 0))
          update
          if (slide > 0)
            Thread.sleep(slide)
        }
      }
    }

    def drawString(s:String, color:(Int, Int, Int), speed:Int=100):Unit=
    {
      import psksvp.Math.RowMajor
      val rowMajor = new RowMajor(8 * s.length, 8)
      val index = 0 to 7

      def makeFrame(s:String):Array[Char]=
      {
        import psksvp.Symbols.AsciiBitmap
        val bigBits = Array.ofDim[Char](8 * s.length * 8)
        var sx = 0
        for (c <- s)
        {
          var i = 0
          val charBits = AsciiBitmap(c)
          for (y <- index; x <- index)
          {
            bigBits(rowMajor.offset(x + sx, y)) = charBits(i)
            i = i + 1
          }
          sx = sx + 8
        }
        bigBits
      }

      def drawFrame(bits:Array[Char], offset:Int):Unit=
      {
        for (y <- index; x <- index)
        {
          if ('1' == bits(rowMajor.offset(x + offset, y)))
            setPixel(x, y, color)
        }
      }

      val wholeFrame = makeFrame(s)
      for(sx <- 0 to (8 * s.length) - 8)
      {
        clear
        drawFrame(wholeFrame, sx)
        update
        Thread.sleep(speed)
      }
    }
  }

  private var ledDisplay:Option[LEDDisplay] = None
  def display:LEDDisplay=
  {
    def findFrameBufferDevicePath:Option[String]=
    {
      import psksvp.FileSystem.{ListFiles, SimpleFileIO}
      import java.io.File
      for(dir <- ListFiles(dir="/sys/class/graphics/", deep=false))
      {
        if (dir.isDirectory &&  0 == dir.getName.indexOf("fb"))
        {
          val nameFile = dir.getAbsolutePath + File.separator + "name"
          val content = SimpleFileIO.readStringFromFile(nameFile)
          if(content.trim == "RPi-Sense FB")
            return Some(File.separator + "dev" + File.separator + dir.getName)
        }
      }
      None
    }

    ledDisplay match
    {
      case Some(led) => led
      case None      =>
        findFrameBufferDevicePath match
        {
          case Some(path) => ledDisplay = Some(new LEDDisplay(path))
                             ledDisplay.get
          case None       => sys.error("frame buffer does not exist at SenseHat.LEDDisplay")
        }
    }
  }

  //////////////////// IMU ///////////////////////
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
    }

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

  private lazy val sensorDevice = new SensorDevice
  private lazy val sensorsReady = sensorDevice.init
  def sensors:SensorDevice=
  {
    if(sensorsReady)
      sensorDevice
    else
      sys.error("SenseHat.sensors hardware init fail")
  }

  //////////////////// stick ///////////////////////
  final val kUP = 103
  final val kLEFT = 105
  final val kRIGHT = 106
  final val kDOWN = 108
  final val kENTER = 28
  class Stick(devicePath:String)
  {
    private final val evKey = 0x01
    private val inputBuffer = Array.ofDim[Byte](16)

    /**
      * TODO, implement non-blocking read
      * blocking read
      * @return key code, up -> 103, left -> 105, right -> 106, down -> 108, enter (push) -> 28
      */
    def read:Int=
    {
      import java.io.FileInputStream
      import psksvp.Math.FromBytes
      /*
        each input is 16 bytes.
        long,long,short,    short,  int
        time,time,inputType,keyCode,value
       */
      val devFile = new FileInputStream(devicePath)
      devFile.read(inputBuffer)
      devFile.close

      val inputType = FromBytes.makeShort(lo=inputBuffer(9), hi=inputBuffer(8))
      val keyCode = FromBytes.makeShort(lo=inputBuffer(11), hi=inputBuffer(10))
      if(evKey == inputType)
        keyCode
      else
        -1
    }
  }

  private var stickDev:Option[Stick] = None
  def stick:Stick=
  {
    def findJoyStickDevicePath:Option[String]=
    {
      import psksvp.FileSystem.{ListFiles, SimpleFileIO}
      import java.io.File
      for(dir <- ListFiles(dir="/sys/class/input/", deep=false))
      {
        if (dir.isDirectory && 0 == dir.getName.indexOf("event"))
        {
          val nameFile = dir.getAbsolutePath + "/device/name"
          val content = SimpleFileIO.readStringFromFile(nameFile)
          if(content.trim == "Raspberry Pi Sense HAT Joystick")
            return Some("/dev/input/" + dir.getName)
        }
      }
      None
    }

    stickDev match
    {
      case Some(stickInstance) => stickInstance
      case None =>
        findJoyStickDevicePath match
        {
          case Some(path) => stickDev = Some(new Stick(path))
                             stickDev.get
          case None       => sys.error("There is no sense stick to be found")
        }
    }

  }
}

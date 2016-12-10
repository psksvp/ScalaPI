package psksvp.RPi

/**
  * Created by psksvp on 3/12/16.
  * Base on https://bitbucket.org/thinkbowl/i2clibraries/src/037e6107a866eb6406fced4d0fbef197f2f131a0/i2c_hmc5883l.py?at=master&fileviewer=file-view-default
  */
class HMC5883L(address:Int=0x1e,
               declinationDegree:Float = 6,
               declinationMin:Float = 0,
               gauss:Float=1.3f)
{
  val ConfigurationRegisterA = 0x00
  val ConfigurationRegisterB = 0x01
  val ModeRegister = 0x02
  val AxisXDataRegisterMSB:Byte = 0x03
  val AxisXDataRegisterLSB:Byte = 0x04
  val AxisZDataRegisterMSB:Byte = 0x05
  val AxisZDataRegisterLSB:Byte = 0x06
  val AxisYDataRegisterMSB:Byte = 0x07
  val AxisYDataRegisterLSB:Byte = 0x08
  val StatusRegister:Byte = 0x09
  val IdentificationRegisterA:Byte = 0x10
  val IdentificationRegisterB:Byte = 0x11
  val IdentificationRegisterC:Byte = 0x12
  val MeasurementContinuous:Byte = 0x00
  val MeasurementSingleShot:Byte = 0x01
  val MeasurementIdle:Byte = 0x03

  val declination = (declinationDegree + declinationMin / 60f) * (Math.PI / 180f)


  val scaleMap = Map(0.88f -> (0x00, 0.73f),
                      1.3f -> (0x01, 0.92f),
                      1.9f -> (0x02, 1.22f),
                      2.5f -> (0x03, 1.52f),
                      4.0f -> (0x04, 2.27f),
                      4.7f -> (0x05, 2.56f),
                      5.6f -> (0x06, 3.03f),
                      8.1f -> (0x07, 4.35f))

  val endPoint = I2C.makeConnection(address)
  val scaleReg = ((scaleMap(gauss)._1 << 5) | 0x00).toByte
  endPoint.write(ConfigurationRegisterB, scaleReg)


  def setContinuousMode:Unit=endPoint.write(ModeRegister, MeasurementContinuous)

  def heading:(Double, Double) =
  {
    val (x, y, z) = axes
    val heading = Math.atan2(y, x) + declination
    val headingRad = if(heading < 0)
                       heading + (2 * Math.PI)
                     else
                       heading - (2 * Math.PI)

    val headingDeg = headingRad * 180 / Math.PI
    val degrees = Math.floor(headingDeg)
    val minutes = (heading - degrees) * 60
    (degrees, minutes)
  }

  def axes:(Int, Int, Int) =
  {
    def toInt(value:Int, len:Int):Int =
    {
      if((value & (1 << len - 1)) != 0)
        value - (1 << len)
      else
        value
    }

    val data = Array.ofDim[Int](6)
    data(0) = endPoint.read(AxisXDataRegisterMSB).toInt
    for(i <- 1 until 6)
    {
      data(i) = endPoint.read.toInt
    }

    val int1 = (data(0) << 8) | data(1)
    val int2 = (data(2) << 8) | data(3)
    val int3 = (data(4) << 8) | data(5)

    (toInt(int1, 16), toInt(int2, 16), toInt(int3, 16))
  }

}

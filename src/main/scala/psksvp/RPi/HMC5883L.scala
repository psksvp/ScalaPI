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
  val scale = scaleMap(gauss)._2
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

  def axes:(Float, Float, Float) =
  {
    //endPoint.write(AxisXDataRegisterMSB, 0x3c)
    val hiX = endPoint.read
    val loX = endPoint.read
    val hiZ = endPoint.read
    val loZ = endPoint.read
    val hiY = endPoint.read
    val loY = endPoint.read

    import java.nio.ByteBuffer

    val buffer = ByteBuffer.allocate(2)
    buffer.put(hiX)
    buffer.put(loX)
    val magx = buffer.getShort(0)

    buffer.clear()

    buffer.put(hiZ)
    buffer.put(loZ)
    val magz = buffer.getShort(0)

    buffer.clear()

    buffer.put(hiY)
    buffer.put(loY)
    val magy = buffer.getShort(0)


    (magx * scale, magy * scale, magz * scale)
  }

}

object HMC5883L
{
  def apply():HMC5883L = new HMC5883L()

  def main(args:Array[String]):Unit=
  {
    val h = new HMC5883L()
    h.setContinuousMode
    while(true)
    {
      println(h.axes)
      println("heading " + h.heading)
      Thread.sleep(1000)
    }
  }
}

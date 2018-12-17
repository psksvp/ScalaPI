/*
 *  The BSD 3-Clause License
 *  Copyright (c) 2018. by Pongsak Suvanpong (psksvp@gmail.com)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 *  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This information is provided for personal educational purposes only.
 *
 * The author does not guarantee the accuracy of this information.
 *
 * By using the provided information, libraries or software, you solely take the risks of damaging your hardwares.
 */

package psksvp.RPi

/**
  * Created by psksvp on 3/12/16.
  * Base on https://bitbucket.org/thinkbowl/i2clibraries/src/037e6107a866eb6406fced4d0fbef197f2f131a0/i2c_hmc5883l.py?at=master&fileviewer=file-view-default
  */
class HMC5883L(address:Int=0x1e,
               declinationDegree:Float = 0,
               declinationMin:Float = 6,
               gauss:Float=1.3f)
{
  val ConfigurationRegisterA:Byte = 0x00
  val ConfigurationRegisterB :Byte = 0x01
  val ModeRegister:Byte = 0x02
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
  val scaleReg = (scaleMap(gauss)._1 << 5).toByte
  val scale = scaleMap(gauss)._2
  endPoint.write(ConfigurationRegisterB, scaleReg)
  setContinuousMode


  def setContinuousMode:Unit=endPoint.write(ModeRegister, MeasurementContinuous)

  def heading:(Float, Float) =
  {
    val (x, y, z) = axes
    var headingRad = Math.atan2(y, x)
    headingRad += declination
    if(headingRad < 0)
       headingRad += (2 * Math.PI)

    if(headingRad >= 2 * Math.PI)
      headingRad -= (2 * Math.PI)

    val headingDeg = headingRad * 180 / Math.PI
    val degrees = Math.floor(headingDeg)
    val minutes = Math.round((headingDeg - degrees) * 60)
    (degrees.toFloat, minutes.toFloat)
  }

  def axes:(Float, Float, Float) =
  {
    val msbx = endPoint.read(AxisXDataRegisterMSB)
    val lsbx = endPoint.read(AxisXDataRegisterLSB)
    val msbz = endPoint.read(AxisZDataRegisterMSB)
    val lsbz = endPoint.read(AxisZDataRegisterLSB)
    val msby = endPoint.read(AxisYDataRegisterMSB)
    val lsby = endPoint.read(AxisYDataRegisterLSB)


    import psksvp.Bits.s16From2Bytes
    val magx = s16From2Bytes(msbx, lsbx)
    val magz = s16From2Bytes(msbz, lsbz)
    val magy = s16From2Bytes(msby, lsby)

    import psksvp.Math.round
    (round(magx * scale, 4), round(magy * scale, 4), round(magz * scale, 4))
  }

}

object HMC5883L
{
  import psksvp.Geometry.Data._

  implicit def toHeading(p:(Float, Float)):Heading = Heading(p._1, p._2)
  implicit def toOrientation(o:(Float, Float, Float)):Orientation = Orientation(o._1, o._2, o._3)

  def apply(address:Int=0x1e,
            declinationDegree:Float = 0,
            declinationMin:Float = 6,
            gauss:Float=1.3f):HMC5883L = new HMC5883L(address, declinationDegree, declinationMin, gauss)

  def main(args:Array[String]):Unit=
  {
    val h = new HMC5883L()
    while(true)
    {
      psksvp.Terminal.ANSI.clearScreen
      psksvp.Terminal.ANSI.setCursor(0, 0)
      println("heading " + h.heading)
      println("x, y, z " + h.axes)
      Thread.sleep(500)
    }
  }
}


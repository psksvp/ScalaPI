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

import com.pi4j.io.i2c.{I2CBus, I2CFactory}

/**
  * Created by psksvp on 26/11/2015.
  *
  * this class controls adafruit PWM hat
  * https://www.adafruit.com/products/2327
  *
  */
class PWMHat(i2cAddress:Int=0x40, busNumber:Int=I2CBus.BUS_1)
{
  private val ports = Array.ofDim[Option[PWMDevice]](16)
  for(i <- (0 to ports.length - 1))
    ports(i) = None

  def attachDevice(device:PWMDevice, channel:Int):Unit =
  {
    ports(channel) match
    {
      case None         => ports(channel) = Some(device)
                           device.set(Some(this), channel)

      case Some(device) => sys.error("PWMHat.attachDevice channel " + " is not available")
    }
  }

  def detachDevice(device:PWMDevice):Unit=
  {
    getChannelOfDevice(device) match
    {
      case Some(channel) => ports(channel).get.set(None, -1)
                            ports(channel) = None
      case _             =>
    }
  }

  def getChannelOfDevice(pwmDevice: PWMDevice):Option[Int]=
  {
    for(i <- (0 to ports.length - 1))
    {
      if(ports(i).get == pwmDevice)
        return Some(i)
    }
    None
  }

  /////////////////////////////////////////////////////
  final val kMode1       = 0x00
  final val kMode2       = 0x01
  final val kSubAddr1    = 0x02
  final val kSubAddr2    = 0x04
  final val kSumAddr3    = 0x04
  final val kPreScale    = 0xFE
  final val kOnLow       = 0x06
  final val kOnHigh      = 0x07
  final val kOffLow      = 0x08
  final val kOffHigh     = 0x09
  final val kAllOnLow    = 0xFA
  final val kAllOnHigh   = 0xFB
  final val kAllOffLow   = 0xFC
  final val kAllOffHigh  = 0xFD

  //Bits
  final val kSleep:Byte   = 0x10
  final val kAllCall:Byte = 0x01
  final val kInvert:Byte  = 0x10
  final val kOutDrv:Byte  = 0x04

  private val device = I2CFactory.getInstance(busNumber).getDevice(i2cAddress)
  init

  def init:Unit=
  {
    sendToAllChannel(0, 0)
    device.write(kMode2, kOutDrv)
    device.write(kMode1, kAllCall)
    Thread.sleep(5)
    val mode1 = device.read(kMode1) & kSleep
    device.write(kMode1, mode1.toByte)
    Thread.sleep(5)
    setFrequency(60)
  }

  def setFrequency(freq:Double):Unit=
  {
    var prescale = 25000000.0     //25MHz
    prescale /= 4096.0   //12-bit
    prescale /= freq
    prescale -= 1.0
    prescale = Math.floor(prescale + 0.5)
    val oldmode = device.read(kMode1)
    val newmode = (oldmode & 0x7F) | 0x10
    device.write(kMode1, newmode.toByte)
    device.write(kPreScale, (Math.floor(prescale)).toByte)
    device.write(kMode1, oldmode.toByte)
    Thread.sleep(5)
    device.write(kMode1, (oldmode | 0x80).toByte)
  }

  def send(channel:Int, on:Int, off:Int):Unit=
  {
    device.write(kOnLow + 4 * channel, (on & 0xff).toByte)
    device.write(kOnHigh + 4 * channel, (on >> 8).toByte)

    device.write(kOffLow + 4 * channel, (off & 0xff).toByte)
    device.write(kOffHigh + 4 * channel, (off >> 8).toByte)
  }

  def sendToAllChannel(on:Int, off:Int):Unit=
  {
    device.write(kAllOnLow, (on & 0xFF).toByte)
    device.write(kAllOnHigh, (on >> 8).toByte)

    device.write(kAllOffLow, (off & 0xFF).toByte)
    device.write(kAllOffHigh, (off >> 8).toByte)
  }
}

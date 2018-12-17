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
  * Created by psksvp on 5/12/2015.
  */
abstract class AdaFruitPWMI2CBoard[T <: PWMDevice](nChennels:Int, i2cAddress:Int)
{
  import scala.reflect.ClassTag
  private val ports = Array.ofDim[Option[T]](16)
  for(i <- ports.indices)
    ports(i) = None

  def pwmController:PWMI2CEndPoint

  def attachDevice[D:ClassTag](channel:Int):D =
  {
    require(channel >= 0 && channel < numberOfChannels)
    ports(channel) match
    {
      case None => PWMDevice.createDevice[D] match
                   {
                      case Some(device) => ports(channel) = Some(device.asInstanceOf[T])
                                           device.asInstanceOf[T].init(Some(pwmController), channel)
                                           println("attach " + device + " at channel " + channel)
                                           device
                      case None         => sys.error("AdaFruitPWMI2CBoard::attachDevice fail. May need to call PWMDevice.using")
                   }


      case _    => sys.error("PWMHat.attachDevice channel " + channel + " is not available")
    }
  }

  def detachDevice(device:T):Unit=
  {
    getChannelOfDevice(device) match
    {
      case Some(channel) => ports(channel).get.init(None, -1)
                            ports(channel) = None
                            println("device " + device + " was detached")
      case _             =>
    }

    println("device " + device + " was NOT attached with this board " + this)
  }

  def getChannelOfDevice(pwmDevice: T):Option[Int]=
  {
    for(i <- ports.indices)
    {
      ports(i) match
      {
        case Some(device) =>
          if(device == pwmDevice)
            Some(i)
        case _ =>
      }
    }
    None
  }

  def numberOfChannels:Int = nChennels
  def address:Int = i2cAddress
}

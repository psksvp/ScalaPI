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

import scala.collection.immutable.Range
/**
  * Created by psksvp on 2/12/2015.
  */


abstract class PWMDevice
{
  private var pwm:Option[PWMController] = None
  private var channel = -1

  def init(h:Option[PWMController], ch:Int):Unit=
  {
    pwm = h
    channel = ch
  }

  def pwmController=pwm
  def pwmChannel=channel
}
/**
  *
  * @param rawRange
  * @param logicalRange
  */
                                             // unsure why 150 to 600, just copy from adafruit code
abstract class RangePWMDevice(logicalRange:Range, rawRange:Range=(150 to 600)) extends PWMDevice
{
  import psksvp.Math.ScaleValue
  private val scale = new ScaleValue(logicalRange.min, logicalRange.max, rawRange.min, rawRange.max)

  def set(value:Int):Unit=
  {
    pwmController match
    {
      case Some(pwm) => pwm.send(pwmChannel, 0, scale(value).toInt)
      case None      => println(this + " this RangePWMDevice has not been attached to any PWMController")
    }
  }
}

abstract class MotorPWMDevice extends PWMDevice

case class Servo(armAngleRange:Range) extends RangePWMDevice(armAngleRange)
case class ESC() extends RangePWMDevice(-128 to 128)

case class DCMotor() extends MotorPWMDevice
{
  final val kFORWARD = 1
  final val kBACKWARD = 2
  final val kBRAKE = 3
  final val kRELEASE = 4

  private val speedLimit = psksvp.Math.Limit[Int](0, 255)
  private var pwmPin = 0
  private var in1Pin = 0
  private var in2Pin = 0

  override def init(h:Option[PWMController], ch:Int):Unit=
  {
    super.init(h, ch)
    ch match
    {
      case 0 => pwmPin = 8
                in1Pin = 9
                in2Pin = 10
      case 1 => pwmPin = 13
                in1Pin = 12
                in2Pin = 11
      case 2 => pwmPin = 2
                in1Pin = 3
                in2Pin = 4
      case 3 => pwmPin = 7
                in1Pin = 6
                in2Pin = 5
      case _ =>
    }
  }

  def forward()=run(kFORWARD)
  def backward()=run(kBACKWARD)
  def release()=run(kRELEASE)

  def run(command:Int):Unit=
  {
    pwmController match
    {
      case Some(pwm) =>
        command match
        {
          case `kFORWARD`  => pwm.setPin(in2Pin, 0)
                              pwm.setPin(in1Pin, 1)
          case `kBACKWARD` => pwm.setPin(in1Pin, 0)
                              pwm.setPin(in2Pin, 1)
          case `kRELEASE`  => pwm.setPin(in1Pin, 0)
                              pwm.setPin(in2Pin, 0)
        }
      case None      => println(this + " this DCMotor has not been attached to any PWMController")
    }
  }

  def setSpeed(s:Int):Unit=
  {
    pwmController match
    {
      case Some(pwm) => pwm.send(pwmPin, 0, speedLimit(s) * 16)
      case None      => println(this + " this DCMotor has not been attached to any PWMController")
    }
  }
}


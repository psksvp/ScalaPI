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

/**
  *
  * @param rawRange
  * @param logicalRange
  */
abstract class PWMDevice(rawRange:Range, logicalRange:Range)
{
  import psksvp.Math.ScaleValue
  private val scale = new ScaleValue(logicalRange.min, logicalRange.max, rawRange.min, rawRange.max)
  private var hat:Option[PWMHat] = None
  private var channel = -1

  def set(h:Option[PWMHat], ch:Int):Unit=
  {
    hat = h
    channel = ch
  }

  def set(value:Int):Unit=
  {
    hat match
    {
      case Some(pwm) => pwm.send(channel, 0, scale(value).toInt)
      case None      => println(this + " this PWMDevice has not been attached with PWMHat")
    }
  }
}

/**
  *
  * @param armAngleRange
  */
case class Servo(armAngleRange:Range) extends PWMDevice(150 to 600, armAngleRange)

case class ESC() extends PWMDevice(150 to 600, -128 to 128)

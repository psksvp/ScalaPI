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
package psksvp.Math

/**
  * Created by psksvp on 17/11/2015.
  */
class PID(setPoint:Double,
          kp:Double = 0.0,
          ki:Double = 0.0,
          kd:Double = 0.0,
          outputLimit:Limit[Double])
{
  private var kP = kp
  private var kI = ki
  private var kD = kd
  private var kSetPoint = setPoint
  private var integral = 0.0
  private var derivative = 0.0

  def apply(input:Double)=computeDelta(input)

  /**
    *
    * @param input
    * @return delta (change amount) to reach setPoint
    */
  def computeDelta(input:Double):Double=
  {
    val error = kSetPoint - input
    val p = kP * error
    val d = kD * (error - derivative)
    integral = outputLimit(integral + error)
    derivative = error
    val i = integral * kI
    p + i + d
  }

  def setPID(p:Double, i:Double, d:Double):Unit=
  {
    kP = p
    kI = i
    kD = d
  }

  def setSetPoint(sp:Double):Unit=
  {
    kSetPoint = sp
  }
}

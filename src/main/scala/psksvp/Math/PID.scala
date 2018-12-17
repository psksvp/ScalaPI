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
package psksvp.Math


case class PID(setPoint:Double,
               kP:Double,
               kI:Double,
               kD:Double,
               outputLimit:Limit[Double],
               integral:Double = 0.0,
               derivative:Double = 0.0)
{
  def step(withInput:Double):(Double, PID) =
  {
    val error = this.setPoint - withInput
    val p = this.kP * error
    val d = this.kD * (error - this.derivative)
    val nextIntegral = this.outputLimit(this.integral + error)
    val nextDerivative = error
    val i = nextIntegral * this.kI
    val nextPID = this.copy(integral = nextIntegral, derivative = nextDerivative)
    (p + i + d, nextPID)
  }
}

class PIDArray(pids:PID*)
{
  private val mpids = pids.toArray

  def step(inputs:Double*):Seq[Double]=
  {
    require(inputs.length == pids.length, "PIDArray.step  inputs.length != pids.length")
    val outputs = Array.ofDim[Double](inputs.length)
    for(i <- pids.indices)
    {
      val (output, pid) = mpids(i).step(inputs(i))
      outputs(i) = output
      mpids(i) = pid
    }
    outputs
  }

  def updateSetPoint(setPoints:Double*):Unit=
  {
    require(setPoints.length == pids.length, "PIDArray.updateSetPoint  setPoints.length != pids.length")
    for(i <- pids.indices)
    {
      mpids(i) = mpids(i).copy(setPoint = setPoints(i))
    }
  }
}




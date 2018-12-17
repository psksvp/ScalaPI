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

/**
 * Created by psksvp on 2/05/2014.
Time (Month)	        0	2	4	6	8	10
analytical population	100	122.14	149.182	182.212	222.554	271.828
growth	10	12	14.4	17.28	20.736	24.8832
population	100	120	144	172.8	207.36	248.832

 */
object ODESolver
{
  def euler(valueAtStart: Double,
             tStart: Double,
             tEnd: Double,
             stepSize: Double,
             changeFunction: (Double, Double) => Double) : Double =
  {
    require(tStart <= tEnd)
    var T = tStart
    var y = valueAtStart
    do
    {
      val changes = changeFunction(y, T)
      y += changes * stepSize
      T += stepSize
    }while(T <= tEnd)
    y
  }

  def RK2(valueAtStart: Double,
          tStart: Double,
          tEnd: Double,
          stepSize: Double,
          changeFunction: (Double, Double) => Double) : Double =
  {
    var T = tStart
    var Pn = valueAtStart
    do
    {
      val changes1 = changeFunction(Pn, T)
      val Yn = Pn + (changes1 * stepSize)
      T += stepSize
      val changes2 = changeFunction(Yn, T)
      Pn += (0.5 * stepSize * (changes1 + changes2))
    }while(T <= tEnd)
    Pn
  }

  def RK4(valueAtStart: Double,
          tStart: Double,
          tEnd: Double,
          stepSize: Double,
          changeFunction: (Double, Double) => Double) : Double =
  {
    var y = valueAtStart
    var t = tStart
    val h = stepSize
    do
    {
      val f1 = changeFunction(t, y)
      val f2 = changeFunction(t + (h / 2), y + (h / 2) * f1)
      val f3 = changeFunction(t + (h / 2), y + (h / 2) * f2)
      val f4 = changeFunction(t + h, y + h * f3)
      y = y + (h / 6) * (f1 + 2 * f2 + 2 * f3 + f4)
      t = t + h;
    }while(t <= tEnd)
    y
  }
}

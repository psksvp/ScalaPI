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

package psksvp

/**
  * Created by psksvp on 11/12/16.
  */
package object Math
{
  /**
    * http://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals
    * @param d
    * @param decimalPlace
    * @return
    */
  def round(d:Float, decimalPlace:Int):Float =
  {
    val bd = new java.math.BigDecimal(java.lang.Float.toString(d))
    bd.setScale(decimalPlace, java.math.BigDecimal.ROUND_HALF_UP).floatValue()
  }

  /**
    *
    * @param a
    * @param b
    * @return
    */
  def gcd(a:Int, b:Int):Int=
  {
    if(a < b)
      gcd(b, a)
    else if(0 == b)
      a
    else
      gcd(b, a % b)
  }

  /**
    *
    * @param a
    * @param b
    * @return
    */
  def lcm(a:Int, b:Int):Int = (a * b) / gcd(a, b)



  /**
    *
    * @param a
    * @param b
    * @return
    */
  def modulo(a:Int, b:Int):Int=
  {
    var result = a % b
    if( result < 0)
      result = result + b

    result
  }

  def clamp(value:Int, lo:Int, high:Int):Int =
  {
    if(value < lo) lo
    else if(value > high) high
    else value
  }

  // Floating point linear interpolation function that takes a value inside one
  // range and maps it to a new value inside another range.  This is used to transform
  // each axis of acceleration to mouse velocity/speed. See this page for details
  // on the equation: https://en.wikipedia.org/wiki/Linear_interpolation
  def linearInterpolation(x:Double, x0:Double, x1:Double, y0:Double, y1:Double): Double =
  {
    if(x <= x0) y0
    else if(x >= x1) y1
    else y0 + (y1 - y0) * ((x - x0) / (x1 - x0))
  }

  def euclideanDistance(a:Seq[Double], b:Seq[Double]):Double =
  {
    val d = for((e1, e2) <- a zip b) yield (e1 - e2) * (e1 - e2)
    scala.math.sqrt(d.sum)
  }
}

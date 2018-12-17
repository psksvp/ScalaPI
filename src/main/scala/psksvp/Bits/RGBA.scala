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

package psksvp.Bits

class RGBA(pixel:Int)
{
  lazy val alpha:Int = (pixel >> 24) & 0xff
  lazy val red:Int   = (pixel >> 16) & 0xff
  lazy val green:Int = (pixel >>  8) & 0xff
  lazy val blue:Int  = (pixel      ) & 0xff
  lazy val asIntArray:Array[Int] = Array[Int](red, green, blue, alpha)
  lazy val asFloat64Array:Array[Double] = Array[Double](red, green, blue, alpha)
  lazy val asFloat64RGB:Array[Double] = Array[Double](red, green, blue)

  lazy val rgb:(Int, Int, Int) = (red, green, blue)
  lazy val rgba:(Int, Int, Int, Int) = (red, green, blue, alpha)
}

object RGBA
{
  def apply(pixel:Int):RGBA = new RGBA(pixel)
  def apply(red:Int, green:Int, blue:Int, alpha:Int = 255):Int = (alpha << 24) | (red << 16) | (green << 8) | blue

  lazy val black:Int = apply(0, 0, 0)
  lazy val white:Int = apply(255, 255, 255)
  def red(intensity:Int):Int = apply(intensity, 0, 0)
  def green(intensity:Int):Int = apply(0, intensity, 0)
  def blue(intensity:Int):Int = apply(0, 0, intensity)
}
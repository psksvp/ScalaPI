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
  * Created by psksvp on 10/12/16.
  */
package object Bits
{
  def longToWords(n:Long):(Int, Int) = ((n >> 32).toInt, n.toInt)
  def doubleToWords(n:Double):(Int, Int) = longToWords(java.lang.Double.doubleToRawLongBits(n))
  def wordsToLong(high:Int, low:Int):Long = high.toLong << 32 | low & 0xFFFFFFFFL
  def wordsToDouble(high:Int, low:Int):Double = java.lang.Double.longBitsToDouble(wordsToLong(high, low))
  def floatToWord(n:Float):Int = java.lang.Float.floatToIntBits(n)
  def wordToFloat(n:Int):Float = java.lang.Float.intBitsToFloat(n)

  def s16From2Bytes(high:Byte, low:Byte):Short = (high<<8 | low & 0xFF).toShort


  def makeWordsFrom(longValue:Long):List[Int]=
  {
    val (h, l) = longToWords(longValue)
    List(h, l)
  }

  def makeWordsFrom(doubleValue:Double):List[Int]=
  {
    val (h, l) = doubleToWords(doubleValue)
    List(h, l)
  }

  def makeDoubleFromWords(high:Int, low:Int):Double = wordsToDouble(high, low)
  def makeLongFromWords(high:Int, low:Int):Long = wordsToLong(high, low)
}

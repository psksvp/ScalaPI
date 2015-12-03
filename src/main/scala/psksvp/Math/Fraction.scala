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
 * Created by psksvp on 17/05/2014.
 */
class Fraction(P:Int, Q:Int)
{
  require(Q != 0)

  def numerator=P
  def denominator=Q
  def toDoublet: Double=numerator/denominator
  def toFloat: Float=numerator/denominator

  def + (a: Fraction): Fraction =
  {
    val N = this.numerator * a.denominator + this.denominator * a.numerator
    val M = this.denominator * a.denominator
    new Fraction(N, M)
  }

  def - (a: Fraction): Fraction =
  {
    val N = this.numerator * a.denominator - this.denominator * a.numerator
    val M = this.denominator * a.denominator
    new Fraction(N, M)
  }

  def * (a: Fraction): Fraction = new Fraction(this.numerator * a.numerator,this.denominator * a.denominator)
  def / (a: Fraction): Fraction = new Fraction(this.numerator * a.denominator, this.denominator * a.numerator)
  def ** (n: Int) : Fraction =
  {
    require(n != 0)

    val a = math.pow(this.numerator, n).toInt
    val b = math.pow(this.denominator, n).toInt

    if(n > 0)
      new Fraction(a, b)
    else
      new Fraction(b, a)
  }

  def == (a: Fraction): Boolean = this.numerator * a.denominator == this.denominator * a.numerator
  def < (a: Fraction): Boolean = this.numerator * a.denominator < this.denominator * a.numerator
  def > (a: Fraction): Boolean = this.numerator * a.denominator > this.denominator * a.numerator

  override def toString: String =  "[" + this.numerator + "/" + this.denominator + "]"
}

object Fraction
{
  def apply(P:Int, Q:Int):Fraction = new Fraction(P, Q)
}

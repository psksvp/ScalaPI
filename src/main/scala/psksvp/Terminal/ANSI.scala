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

package psksvp.Terminal

/**
  * Created by psksvp on 20/12/2015.
  */
object ANSI
{
  final val esc = "\033["
  final val black = 30
  final val red = 31
  final val green = 32
  final val yellow = 33
  final val blue = 34
  final val magenta = 35
  final val cyan = 36
  final val white = 37
  final val normal = 0
  final val bold = 1
  final val underline = 4
  final val blink = 5
  final val reverse = 7
  final val concealed = 8


  def setCursor(row:Int, col:Int) = print(s"$esc$row;$col" + 'H')
  def cursorUp(n:Int) = print(s"$esc$n" + 'A')
  def cursorDown(n:Int) = print(s"$esc$n" + 'B')
  def cursorForward(n:Int) = print(s"$esc$n" + 'C')
  def cursorBackward(n:Int) = print(s"$esc$n" + 'D')
  def clearScreen = print(esc + "2J")
  def clearLine = print(esc + "K")
  def setForegroundColor(c:Int) = print(esc + c + "m")
  def setBackgroundColor(c:Int) = print(esc + (c+10) + "m")
  def setTextAttribute(a:Int) = print(esc + a + "m")
}

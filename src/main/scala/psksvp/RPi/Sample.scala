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

package psksvp.RPi

/**
  * Created by psksvp on 18/12/2015.
  */
object Sample
{
  def runLife(nGeneration:Int):Unit=
  {
    import psksvp.Toy.CA._

    def draw(life:LifeWorld):Unit=
    {
      val index = 0 until 8
      for(r <- index; c <- index)
      {
        life(r, c) match
        {
          case 0 => SenseHAT.display.setPixel(c, r, (10, 10, 10))
          case 1 => SenseHAT.display.setPixel(c, r, (0, 255, 0))
          case 2 => SenseHAT.display.setPixel(c, r, (100, 0, 0))
        }
      }

      SenseHAT.display.update
    }


    val life = new LifeWorld(worldSize = 8, rule = new Conway)
    draw(life)
    Thread.sleep(200)
    for(i <- (0 until nGeneration))
    {
      life.run
      draw(life)
      Thread.sleep(200)
      println("generation " + i)
    }
    SenseHAT.display.drawString("Done", (0, 244, 0))
    SenseHAT.display.clear
  }

  def servosControl(n:Int = 16):Unit =
  {
    import swing._
    import swing.event._
    import psksvp.GUI.SliderArray

    PWMDevice.initAll()
    val args = Array[String]()
    val pwmHAT = new PWMHAT()
    val servos = Array.ofDim[Servo](n)
    for(i <- 0 until n)
      servos(i) = pwmHAT.attachDevice[Servo](i)

    object GUI extends SimpleSwingApplication
    {
      val top = new MainFrame
      top.title = "Servo Controller"
      val stext = for(s <- 0 until n) yield s.toString
      val srange = for(s <- 0 until n) yield (0 to 180)
      top.contents = new SliderArray(stext, srange, top)
      top.reactions +=
      {
        case ValueChanged(s:Slider) => println("slider " + s.name + "=" + s.value)
                                       servos(s.name.toInt).set(s.value)
        case _                      => println("_")
      }
    }


    GUI.main(args)
  }
}

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

/**
  * Created by psksvp on 11/12/2015.
  */
object GPIO
{
  import psksvp.jni.rpi.{PiGPIO, PiGPIOConstants}

  private var init=false
  try
  {
    if(false == init)
    {
      import psksvp.FileSystem.SimpleFileIO
      SimpleFileIO.loadNativeLibraryFromJar("/native/libPiGPIO.so")
      init = true
    }
  }
  catch
    {
      case e:UnsatisfiedLinkError => sys.error("Native code library failed to load.\n" + e)
    }

  abstract class PinMode(wiringPiCode:Int)
  {
    def code=wiringPiCode
  }
  case class Input() extends PinMode(PiGPIOConstants.INPUT)
  case class Output() extends PinMode(PiGPIOConstants.OUTPUT)
  case class PWMOutput() extends PinMode(PiGPIOConstants.PWM_OUTPUT)
  case class GPIOClock() extends PinMode(PiGPIOConstants.GPIO_CLOCK)

  abstract class Digital
  {
    def value:Int
  }

  case class HIGH() extends Digital
  {
    def value=1
  }

  case class LOW() extends Digital
  {
    def value=0
  }

  implicit def Digital2Int(digital: Digital):Int=digital.value
  implicit def Int2Digital(value: Int):Digital=
  {
    require(1 == value || 0 == value)
    value match
    {
      case 1 => HIGH()
      case 0 => LOW()
    }
  }

  abstract class Pin(number:Int, mode:PinMode)
  {
    PiGPIO.pinMode(number, mode.code)
    def pinNo=number
  }

  class InputPin(number:Int) extends Pin(number, Input())
  {
    def setPUDOff=PiGPIO.pullUpDnControl(number, PiGPIOConstants.PUD_OFF)
    def setPUDDown=PiGPIO.pullUpDnControl(number, PiGPIOConstants.PUD_DOWN)
    def setPUDUp=PiGPIO.pullUpDnControl(number, PiGPIOConstants.PUD_UP)

    def digitalRead=PiGPIO.digitalRead(number)
    def analogRead=PiGPIO.analogRead(number)

  }

  class OutputPin(number:Int, mode:PinMode=Output()) extends Pin(number, mode)
  {
    def digitalWrite(value:Digital)=PiGPIO.digitalWrite(number, value)
    def analogWrite(value:Int)=PiGPIO.analogWrite(number, value)
  }

  class PWMOutputPin extends Pin(1, PWMOutput())
  {
    def write(value:Int):Unit=
    {
      require(value >= 0 && value <= 1024)
      PiGPIO.pwmWrite(pinNo, value)
    }
  }

  class GPIOClockOutputPin extends OutputPin(7, GPIOClock())

  def getOutputPin(pin:Int)= new OutputPin(pin)
  def getInputPin(pin:Int)= new InputPin(pin)
  def getPWMOutputPin= new PWMOutputPin
  def getGPIOClockOutputPin= new GPIOClockOutputPin
}

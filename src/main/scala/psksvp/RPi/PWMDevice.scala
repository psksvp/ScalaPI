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

import scala.collection.immutable.Range
/**
  * Created by psksvp on 2/12/2015.
  */


abstract class PWMDevice
{
  private var pwm:Option[PWMController] = None
  private var channel = -1

  def init(h:Option[PWMController], ch:Int):Unit=
  {
    pwm = h
    channel = ch
  }

  def pwmController=pwm
  def pwmChannel=channel
}
/**
  *
  * @param rawRange
  * @param logicalRange
  */
                                             // unsure why 150 to 600, just copy from adafruit code
abstract class RangePWMDevice(logicalRange:Range, rawRange:Range=(145 to 650)) extends PWMDevice
{
  import psksvp.Math.ScaleValue
  private val scale = new ScaleValue(logicalRange.min, logicalRange.max, rawRange.min, rawRange.max)

  def set(value:Int):Unit=
  {
    pwmController match
    {
      case Some(pwm) => pwm.send(pwmChannel, 0, scale(value).toInt)
      case None      => println(this + " this RangePWMDevice has not been attached to any PWMController")
    }
  }
}

case class Servo(armAngleRange:Range) extends RangePWMDevice(armAngleRange)
case class ESC() extends RangePWMDevice(-128 to 128)

abstract class MotorPWMDevice extends PWMDevice

abstract class MotorCommand
case class Forward() extends MotorCommand
case class Backward() extends MotorCommand
case class Break() extends MotorCommand
case class Release() extends MotorCommand

abstract class SteppingCommand
case class SingleStep() extends SteppingCommand
case class DoubleStep() extends SteppingCommand
case class InterleaveStep() extends SteppingCommand
case class MicroStep() extends SteppingCommand

case class DCMotor() extends MotorPWMDevice
{
  private val speedLimit = psksvp.Math.Limit[Int](0, 255)
  private var pwmPin = 0
  private var in1Pin = 0
  private var in2Pin = 0

  override def init(h:Option[PWMController], ch:Int):Unit=
  {
    require(ch >= 0 && ch < 4)
    super.init(h, ch)
    ch match
    {
      case 0 => pwmPin = 8
                in1Pin = 9
                in2Pin = 10
      case 1 => pwmPin = 13
                in1Pin = 12
                in2Pin = 11
      case 2 => pwmPin = 2
                in1Pin = 3
                in2Pin = 4
      case 3 => pwmPin = 7
                in1Pin = 6
                in2Pin = 5
      case _ =>
    }
  }

  def forward()=run(Forward())
  def backward()=run(Backward())
  def release()=run(Release())

  def run(command:MotorCommand):Unit=
  {
    pwmController match
    {
      case Some(pwm) =>
        command match
        {
          case Forward()  => pwm.setPin(in2Pin, 0)
                             pwm.setPin(in1Pin, 1)
          case Backward() => pwm.setPin(in1Pin, 0)
                             pwm.setPin(in2Pin, 1)
          case Release()  => pwm.setPin(in1Pin, 0)
                             pwm.setPin(in2Pin, 0)
        }
      case None      => println(this + " this DCMotor has not been attached to any PWMController")
    }
  }

  def setSpeed(s:Int):Unit=
  {
    pwmController match
    {
      case Some(pwm) => pwm.send(pwmPin, 0, speedLimit(s) * 16)
      case None      => println(this + " this DCMotor has not been attached to any PWMController")
    }
  }
}

case class StepperMotor(steps:Int=200,
                        microSteps:Int=8,
                        microStepCurve:Array[Int]=Array(0, 50, 98, 142, 180, 212, 236, 250, 255)) extends MotorPWMDevice
{
  private val revSteps = steps
  private var secPerStep = 0.1
  private var steppingCounter = 0
  private var currentStep = 0

  private var PWMA = 8
  private var AIN2 = 9
  private var AIN1 = 10
  private var PWMB = 13
  private var BIN2 = 12
  private var BIN1 = 11

  override def init(h:Option[PWMController], ch:Int):Unit=
  {
    require(0 == ch || 1 == ch)
    super.init(h, ch)
    if(1 == ch)
    {
      PWMA = 2
      AIN2 = 3
      AIN1 = 4
      PWMB = 7
      BIN2 = 6
      BIN1 = 5
    }
  }

  def setSpeed(rpm:Int):Unit=
  {
    secPerStep = 60.0 / (revSteps * rpm)
    steppingCounter = 0
  }

  def oneStep(dir:MotorCommand, style:SteppingCommand):Int=
  {
    var pwmA = 255
    var pwmB = 255

    def singleStep(dir:MotorCommand):Unit=
    {
      val iDir = if(dir == Forward()) 1 else -1
      if((currentStep / (microSteps / 2)) % 2 != 0)
        currentStep = currentStep + (microSteps / 2) * iDir
      else
        currentStep = currentStep + microSteps * iDir
    }

    def doubleStep(dir:MotorCommand):Unit=
    {
      val iDir = if(dir == Forward()) 1 else -1
      if((currentStep / (microSteps / 2)) % 2 == 0)
        currentStep = currentStep + (microSteps / 2) * iDir
      else
        currentStep = currentStep + microSteps * iDir
    }

    def interleaveStep(dir:MotorCommand):Unit=
    {
      val iDir = if(dir == Forward()) 1 else -1
      currentStep = currentStep + (microSteps / 2) * iDir
    }

    def microStep(dir:MotorCommand):Unit=
    {
      val iDir = if(dir == Forward()) 1 else -1
      currentStep = currentStep + iDir
      currentStep = currentStep + microSteps * 4
      currentStep = currentStep % microSteps * 4
      pwmA = 0
      pwmB = 0
      if(currentStep >= 0 && currentStep < microSteps)
      {
        pwmA = microStepCurve(microSteps - currentStep)
        pwmB = microStepCurve(currentStep)
      }
      else if(currentStep >= microSteps && currentStep < microSteps*2)
      {
        pwmA = microStepCurve(currentStep - microSteps)
        pwmB = microStepCurve(microSteps*2 - currentStep)
      }
      else if(currentStep >= microSteps*2 && currentStep < microSteps*3)
      {
        pwmA = microStepCurve(microSteps * 3 - currentStep)
        pwmB = microStepCurve(currentStep - microSteps * 2)
      }
      else if(currentStep >= microSteps*3 && currentStep < microSteps*4)
      {
        pwmA = microStepCurve(currentStep - microSteps * 3)
        pwmB = microStepCurve(microSteps * 4 - currentStep)
      }
    }

    def coilsControlArray:Array[Int]=
    {
      if(style == MicroStep())
      {
        if (currentStep >= 0 && currentStep < microSteps)
          Array(1, 1, 0, 0)
        else if (currentStep >= microSteps && currentStep < microSteps * 2)
          Array(0, 1, 1, 0)
        else if (currentStep >= microSteps * 2 && currentStep < microSteps * 3)
          Array(0, 0, 1, 1)
        else if (currentStep >= microSteps * 3 && currentStep < microSteps * 4)
          Array(1, 0, 0, 1)
        else
          sys.error("fatal error StepperMotor::oneStep::coilsControlArray code should not reach here")
      }
      else
      {
        val coils = Array(Array(1, 0, 0, 0),
                          Array(1, 1, 0, 0),
                          Array(0, 1, 0, 0),
                          Array(0, 1, 1, 0),
                          Array(0, 0, 1, 0),
                          Array(0, 0, 1, 1),
                          Array(0, 0, 0, 1),
                          Array(1, 0, 0, 1))
        coils(currentStep/(microSteps/2))
      }
    }

    style match
    {
      case SingleStep()     => singleStep(dir)
      case DoubleStep()     => doubleStep(dir)
      case InterleaveStep() => interleaveStep(dir)
      case MicroStep()      => microStep(dir)
    }

    currentStep = currentStep + microSteps * 4
    currentStep = currentStep % microSteps * 4
    pwmController match
    {
      case Some(pwm) => pwm.send(PWMA, 0, pwmA * 16)
                        pwm.send(PWMB, 0, pwmB * 16)
                        val coils = coilsControlArray
                        pwm.setPin(AIN2, coils(0))
                        pwm.setPin(BIN1, coils(1))
                        pwm.setPin(AIN1, coils(2))
                        pwm.setPin(BIN2, coils(3))
                        currentStep
      case None      => sys.error(this + " this StepperMotor has not been attached to any PWMController")
    }
  } // oneStep

  def step(steps:Int, dir:MotorCommand, stepStyle:SteppingCommand):Unit=
  {
    var latestStep = 0
    var delay = secPerStep
    var nsteps = steps
    if(stepStyle == InterleaveStep())
      delay = delay / 2.0
    else if(stepStyle == MicroStep())
    {
      delay = delay / microSteps
      nsteps = nsteps * microSteps
    }

    import psksvp.Loop.loop
    loop(nsteps)
    {
      latestStep = oneStep(dir, stepStyle)
      Thread.sleep((delay * 1000).toInt)
    }

    if(stepStyle == MicroStep())
    {
      while(0 != latestStep && latestStep != microSteps)
      {
        latestStep = oneStep(dir, stepStyle)
        Thread.sleep((delay * 1000).toInt)
      }
    }
  }
}


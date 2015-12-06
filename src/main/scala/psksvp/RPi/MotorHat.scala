package psksvp.RPi

/**
  * Created by psksvp on 4/12/2015.
  */

import com.pi4j.io.i2c.I2CBus
class MotorHAT(frequency:Double=1600.0,
               i2cAddress:Int=0x60,
               i2cBus:Int=I2CBus.BUS_1) extends AdaFruitPWMI2CBoard[MotorPWMDevice](4, i2cAddress, i2cBus)
{
  private val pwm = new PWMController(frequency, i2cAddress, i2cBus)
  override def pwmController=pwm
}

package psksvp.RPi

/**
  * Created by psksvp on 4/12/2015.
  */

class MotorHAT(frequency:Double=1600.0,
               i2cAddress:Int=0x60) extends AdaFruitPWMI2CBoard[MotorPWMDevice](4, i2cAddress)
{
  private val pwm = new PWMController(frequency, i2cAddress)
  override def pwmController=pwm
}

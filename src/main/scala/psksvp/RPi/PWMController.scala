package psksvp.RPi

/**
  * Created by psksvp on 5/12/2015.
  * It is based on python code by adafruit
  * https://github.com/adafruit/Adafruit-Motor-HAT-Python-Library/blob/master/Adafruit_MotorHAT/Adafruit_PWM_Servo_Driver.py
  */

import com.pi4j.io.i2c.{I2CBus, I2CFactory}
class PWMController(frequency:Double, i2cAddress:Int,  i2cBus:Int=I2CBus.BUS_1)
{
  /////////////////////////////////////////////////////
  private val kMode1       = 0x00
  private val kMode2       = 0x01
  private val kSubAddr1    = 0x02
  private val kSubAddr2    = 0x04
  private val kSumAddr3    = 0x04
  private val kPreScale    = 0xFE
  private val kOnLow       = 0x06
  private val kOnHigh      = 0x07
  private val kOffLow      = 0x08
  private val kOffHigh     = 0x09
  private val kAllOnLow    = 0xFA
  private val kAllOnHigh   = 0xFB
  private val kAllOffLow   = 0xFC
  private val kAllOffHigh  = 0xFD

  //Bits
  private val kSleep:Byte   = 0x10
  private val kAllCall:Byte = 0x01
  private val kInvert:Byte  = 0x10
  private val kOutDrv:Byte  = 0x04

  private val generalCallI2C = I2CFactory.getInstance(i2cBus).getDevice(0x00)
  private val device = I2CFactory.getInstance(i2cBus).getDevice(i2cAddress)
  sendToAllChannel(0, 0)
  device.write(kMode2, kOutDrv)
  device.write(kMode1, kAllCall)
  Thread.sleep(5)
  val mode1 = device.read(kMode1) & ~kSleep
  device.write(kMode1, mode1.toByte)
  Thread.sleep(5)
  setFrequency(frequency)

  def setFrequency(freq:Double):Unit=
  {
    var prescale = 25000000.0     //25MHz
    prescale /= 4096.0   //12-bit
    prescale /= freq
    prescale -= 1.0
    prescale = Math.floor(prescale + 0.5)
    val oldmode = device.read(kMode1)
    val newmode = (oldmode & 0x7F) | 0x10
    device.write(kMode1, newmode.toByte)
    device.write(kPreScale, Math.floor(prescale).toByte)
    device.write(kMode1, oldmode.toByte)
    Thread.sleep(5)
    device.write(kMode1, (oldmode | 0x80).toByte)
  }

  def setPin(pin:Int, value:Int): Unit =
  {
    require(pin >= 0 && pin <= 15)
    require(0 == value || 1 == value)
    value match
    {
      case 0 => send(pin, 0, 4096)
      case 1 => send(pin, 4096, 0)
    }
  }

  def send(channel:Int, on:Int, off:Int):Unit=
  {
    device.write(kOnLow + 4 * channel, (on & 0xff).toByte)
    device.write(kOnHigh + 4 * channel, (on >> 8).toByte)

    device.write(kOffLow + 4 * channel, (off & 0xff).toByte)
    device.write(kOffHigh + 4 * channel, (off >> 8).toByte)
  }

  def sendToAllChannel(on:Int, off:Int):Unit=
  {
    device.write(kAllOnLow, (on & 0xFF).toByte)
    device.write(kAllOnHigh, (on >> 8).toByte)

    device.write(kAllOffLow, (off & 0xFF).toByte)
    device.write(kAllOffHigh, (off >> 8).toByte)
  }

  def softwareReset()=generalCallI2C.write(0x06.toByte)
}

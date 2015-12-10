package psksvp.RPi

/**
  * Created by psksvp on 5/12/2015.
  */
abstract class AdaFruitPWMI2CBoard[T <: PWMDevice](nChennels:Int, i2cAddress:Int)
{
  private val ports = Array.ofDim[Option[T]](16)
  for(i <- ports.indices)
    ports(i) = None

  def pwmController:PWMController

  def attachDevice(device:T, channel:Int):Unit =
  {
    require(channel >= 0 && channel < numberOfChannels)
    ports(channel) match
    {
      case None         => ports(channel) = Some(device)
                          device.init(Some(pwmController), channel)

      case _            => sys.error("PWMHat.attachDevice channel " + " is not available")
    }
  }

  def detachDevice(device:T):Unit=
  {
    getChannelOfDevice(device) match
    {
      case Some(channel) => ports(channel).get.init(None, -1)
        ports(channel) = None
      case _             =>
    }
  }

  def getChannelOfDevice(pwmDevice: T):Option[Int]=
  {
    for(i <- ports.indices)
    {
      if(ports(i).get == pwmDevice)
        return Some(i)
    }
    None
  }

  def numberOfChannels=nChennels
  def address=i2cAddress
}

package psksvp.Math

/**
  * Created by psksvp on 4/12/2015.
  */
object FromBytes
{
  def makeShort(lo:Byte, hi:Byte):Short= ( ((lo&0xFF)<<8) | (hi&0xFF) ).toShort
}

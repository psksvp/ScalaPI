package psksvp.Math

/**
  * Created by psksvp on 4/12/2015.
  */
object FromBytes
{
  def makeShort(lo:Byte, hi:Byte):Short= ( ((lo&0xFF)<<8) | (hi&0xFF) ).toShort

  def longToWords(n:Long):(Int, Int) = ((n >> 32).toInt, n.toInt)
  def doubleToWords(n:Double):(Int, Int) = longToWords(java.lang.Double.doubleToRawLongBits(n))
  def wordsToLong(high:Int, low:Int):Long = high.toLong << 32 | low & 0xFFFFFFFFL
  def wordsToDouble(high:Int, low:Int):Double = java.lang.Double.longBitsToDouble(wordsToLong(high, low))
  def floatToWord(n:Float):Int = java.lang.Float.floatToIntBits(n)
  def wordToFloat(n:Int):Float = java.lang.Float.intBitsToFloat(n)
}

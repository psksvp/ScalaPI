package psksvp

/**
  * Created by psksvp on 10/12/16.
  */
package object Bits
{
  def longToWords(n:Long):(Int, Int) = ((n >> 32).toInt, n.toInt)
  def doubleToWords(n:Double):(Int, Int) = longToWords(java.lang.Double.doubleToRawLongBits(n))
  def wordsToLong(high:Int, low:Int):Long = high.toLong << 32 | low & 0xFFFFFFFFL
  def wordsToDouble(high:Int, low:Int):Double = java.lang.Double.longBitsToDouble(wordsToLong(high, low))
  def floatToWord(n:Float):Int = java.lang.Float.floatToIntBits(n)
  def wordToFloat(n:Int):Float = java.lang.Float.intBitsToFloat(n)

  def s16From2Bytes(high:Byte, low:Byte):Short = (high<<8 | low & 0xFF).toShort


  def makeWordsFrom(longValue:Long):List[Int]=
  {
    val (h, l) = longToWords(longValue)
    List(h, l)
  }

  def makeWordsFrom(doubleValue:Double):List[Int]=
  {
    val (h, l) = doubleToWords(doubleValue)
    List(h, l)
  }

  def makeDoubleFromWords(high:Int, low:Int):Double = wordsToDouble(high, low)
  def makeLongFromWords(high:Int, low:Int):Long = wordsToLong(high, low)
}

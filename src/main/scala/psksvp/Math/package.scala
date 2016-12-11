package psksvp

/**
  * Created by psksvp on 11/12/16.
  */
package object Math
{
  /**
    * http://stackoverflow.com/questions/8911356/whats-the-best-practice-to-round-a-float-to-2-decimals
    * @param d
    * @param decimalPlace
    * @return
    */
  def round(d:Float, decimalPlace:Int):Float =
  {
    val bd = new java.math.BigDecimal(java.lang.Float.toString(d))
    bd.setScale(decimalPlace, java.math.BigDecimal.ROUND_HALF_UP).floatValue()
  }
}

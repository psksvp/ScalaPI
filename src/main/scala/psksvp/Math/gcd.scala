package psksvp.Math

/**
  * Created by psksvp on 18/03/2016.
  */
object gcd
{
  def apply(a:Int, b:Int):Int=
  {
    var C = 0
    var A = a
    var B = b
    while(0 != A)
    {
      C = A
      A = B % A
      B = C
    }

    B
  }
}

package psksvp.Math

/**
  * Created by psksvp on 18/03/2016.
  */
object lcm
{
  def apply(a:Int, b:Int):Int = (a * b) / gcd(a, b)
}

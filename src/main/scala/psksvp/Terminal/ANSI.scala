package psksvp.Terminal

/**
  * Created by psksvp on 20/12/2015.
  */
object ANSI
{
  final val esc = "\033["
  final val black = 30
  final val red = 31
  final val green = 32
  final val yellow = 33
  final val blue = 34
  final val magenta = 35
  final val cyan = 36
  final val white = 37
  final val normal = 0
  final val bold = 1
  final val underline = 4
  final val blink = 5
  final val reverse = 7
  final val concealed = 8


  def setCursor(row:Int, col:Int) = s"$esc$row,$col" + 'H'
  def cursorUp(n:Int) = s"$esc$n" + 'A'
  def cursorDown(n:Int) = s"$esc$n" + 'B'
  def cursorForward(n:Int) = s"$esc$n" + 'C'
  def cursorBackward(n:Int) = s"$esc$n" + 'D'
  def clearScreen = esc + "2J"
  def clearLine = esc + "K"
  def setForegroundColor(c:Int) = esc + c + "m"
  def setBackgroundColor(c:Int) = esc + (c+10) + "m"
  def setTextAttribute(a:Int) = esc + a + "m"
}

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


  def setCursor(row:Int, col:Int) = print(s"$esc$row;$col" + 'H')
  def cursorUp(n:Int) = print(s"$esc$n" + 'A')
  def cursorDown(n:Int) = print(s"$esc$n" + 'B')
  def cursorForward(n:Int) = print(s"$esc$n" + 'C')
  def cursorBackward(n:Int) = print(s"$esc$n" + 'D')
  def clearScreen = print(esc + "2J")
  def clearLine = print(esc + "K")
  def setForegroundColor(c:Int) = print(esc + c + "m")
  def setBackgroundColor(c:Int) = print(esc + (c+10) + "m")
  def setTextAttribute(a:Int) = print(esc + a + "m")
}

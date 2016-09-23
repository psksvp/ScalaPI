package psksvp.Concurrency
/**
 * Created by psksvp on 14/07/2014.
 */
object Timer
{
  def apply(interval: Int, repeats: Boolean = true)(op: => Unit)
  {
    val timeOut = new javax.swing.AbstractAction()
    {
      def actionPerformed(e : java.awt.event.ActionEvent) = op
    }
    val t = new javax.swing.Timer(interval, timeOut)
    t.setRepeats(repeats)
    t.start()
  }
}

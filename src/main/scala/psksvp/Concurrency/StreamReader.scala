package psksvp.Concurrency

import java.io._

/**
 * Created by psksvp on 27/10/15.
 */
class StreamReader(is:InputStream, os:OutputStream=null) extends Worker
{
  private var busy = false
  private val buffer = new StringBuilder

  override def isBusy:Boolean = busy

  override def toString:String =
  {
    this.synchronized
    {
      buffer.toString
    }
  }

  override def work():Unit =
  {
    busy = true
    try
    {
      val bufferedReader = new BufferedReader(new InputStreamReader(is))
      var line:String = bufferedReader.readLine()
      while(line  != null)
      {
        this.synchronized
        {
          buffer.append(line + '\n')
          if (os != null)
            os.write(line.getBytes)
        }
        line = bufferedReader.readLine()
      }
    }
    catch
    {
      case e:IOException => println("")
    }
    busy = false
  }
}

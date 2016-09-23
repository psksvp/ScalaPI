package psksvp.Concurrency

/**
 * Created by psksvp on 30/07/2014.
 */
trait Notifiable
{
  def taskDone(worker:Worker):Unit
}

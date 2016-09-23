package psksvp.Concurrency

/**
 * Created by psksvp on 30/07/2014.
 */
trait Worker
{
  def id:Int=0
  def isBusy():Boolean
  def work():Unit
}

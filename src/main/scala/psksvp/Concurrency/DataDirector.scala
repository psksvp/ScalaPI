package psksvp.Concurrency

/**
  * Created by psksvp on 24/11/2015.
  */
trait DataDirector[P, Q]
{
  def getWorkData(worker:Worker):Option[P]
  def putResultData(worker:Worker, data:Q):Unit
}

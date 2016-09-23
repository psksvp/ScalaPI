package psksvp.Concurrency

/**
  * Created by psksvp@gmail.com on 6/11/2015.
  */
class QueuedDataProcessor[P, Q](numberOfWorkers:Int,
                                workFunction:P=>Q) extends DataProcessor
{
  import java.util.concurrent.LinkedBlockingQueue
  import java.util.concurrent.BlockingQueue

  class QueuedDataDirector[P, Q](inputQueue:BlockingQueue[P],
                                 outputQueue:BlockingQueue[Q]) extends DataDirector[P, Q]
  {
    override def getWorkData(worker:Worker):Option[P]=
    {
      val data = inputQueue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS)
      if(null != data) Some(data) else None
    }

    override def putResultData(worker:Worker, data:Q):Unit=outputQueue.put(data)
  }
  ///
  val inputQueue = new LinkedBlockingQueue[P]()
  val outputQueue = new LinkedBlockingQueue[Q]()
  val workGroup =  new Workgroup[P, Q](numberOfWorkers,
                                       new QueuedDataDirector[P, Q](inputQueue, outputQueue),
                                       workFunction)

  override def run=workGroup.start()
  override def stop:Unit=
  {
    workGroup.stop
    workGroup.kill
  }

  def pushWorkData(data:P):Unit=inputQueue.put(data)
  def getResultData:Q=outputQueue.take()
}


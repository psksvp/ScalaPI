package psksvp.Concurrency

/**
  * Created by psksvp on 22/11/2015.
  */
class Workgroup[P, Q](numberOfWorkers:Int, director:DataDirector[P, Q], workFunction:P=>Q)
{
  class Worker[P, Q](wid:Int,
                     director:DataDirector[P, Q],
                     workFunction:P=>Q) extends Runnable with psksvp.Concurrency.Worker
  {
    (new Thread(this)).start()
    var busy = false
    override def isBusy():Boolean=busy
    override def work():Unit=
    {
      while(false == killing)
      {
        while (false == running)
          Thread.sleep(100)
        running = true
        busy = true
        do
        {
          director.getWorkData(this) match
          {
            case Some(data) => director.putResultData(this, workFunction(data))
            case None =>
          }
        } while (false == stopping && false == oneshot)
        busy = false
      }
    }

    override def run():Unit=this.work()
    override def id:Int=wid
  }

  //////////////////////////////////////////////////////////
  private var oneshot = false
  private var killing = false
  private var stopping = false
  private var running = false
  private val workers = new Array[Worker[P,Q]](numberOfWorkers)
  for(i <- 0 to numberOfWorkers - 1)
    workers(i) = new Worker[P, Q](i, director, workFunction)

  def start(oneShot:Boolean=false):Unit=
  {
    oneshot = oneShot
    stopping = false
    running = true
  }

  def stop:Unit=
  {
    stopping = true
    for(worker <- workers)
    {
      while(worker.isBusy())
        Thread.sleep(100)
    }
    running = false
  }

  def kill:Unit=
  {
    killing = true
    running = true
    stop
  }

  def anyBusy:Boolean=
  {
    for(worker <- workers)
    {
      if(true == worker.isBusy())
        return true
    }
    false
  }
}

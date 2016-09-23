package psksvp.Concurrency

/**
 * Created by psksvp on 30/07/2014.
 */
class Task(worker:Worker, notifiable:Notifiable) extends Runnable
{
  (new Thread(this)).start()

  def run():Unit=
  {
    worker.work()
    notifiable.taskDone(worker)
  }
}

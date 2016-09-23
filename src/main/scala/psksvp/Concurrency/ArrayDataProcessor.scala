package psksvp.Concurrency

/**
  * Created by psksvp on 22/11/2015.
  */
class ArrayDataProcessor[P, Q](inputArray:Array[P],
                               outputArray:Array[Q],
                               workFunction:P=>Q) extends DataProcessor
{
  class ArrayDataDirector[P, Q](inputArray:Array[P],
                                outputArray:Array[Q]) extends DataDirector[P, Q]
  {
    override def getWorkData(worker:Worker):Option[P]=Some(inputArray(worker.id))
    override def putResultData(worker:Worker, data:Q):Unit=outputArray(worker.id) = data
  }
  ///
  require(inputArray.size == outputArray.size)
  val workGroup =  new Workgroup[P, Q](inputArray.size,
                                       new ArrayDataDirector[P, Q](inputArray, outputArray),
                                       workFunction)

  override def run=workGroup.start(oneShot = true)
  override def stop=workGroup.kill
}

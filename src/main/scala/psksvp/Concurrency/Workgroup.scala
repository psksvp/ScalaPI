/*
 *  The BSD 3-Clause License
 *  Copyright (c) 2018. by Pongsak Suvanpong (psksvp@gmail.com)
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 *  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This information is provided for personal educational purposes only.
 *
 * The author does not guarantee the accuracy of this information.
 *
 * By using the provided information, libraries or software, you solely take the risks of damaging your hardwares.
 */

package psksvp.Concurrency

/**
  * Created by psksvp on 22/11/2015.
  */
class Workgroup[P, Q](numberOfWorkers:Int,
                      director:DataDirector[P, Q],
                      workFunction:P=>Q)
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
  for(i <- workers.indices)
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

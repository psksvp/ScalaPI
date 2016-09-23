package psksvp.Concurrency

/**
  * Created by psksvp on 25/11/2015.
  */
abstract class DataProcessor
{
  def run:Unit
  def stop:Unit
}

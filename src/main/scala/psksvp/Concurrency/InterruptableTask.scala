package psksvp.Concurrency

/**
  * Created by psksvp on 22/12/2015.
  */
class InterruptableTask(thread:Thread) extends java.util.TimerTask
{
  override def run:Unit=
  {
    println("InterruptableTask going to interrupt")
    thread.interrupt()
    println("InterruptableTask did interrupt")
  }
}


/*
    public static void main(String[] args) throws IOException
    {
        Timer timer = null;

        Process p = Runtime.getRuntime().exec("java -cp /Users/psksvp/Workspace infinite");
        //ProcessBuilder pb = new ProcessBuilder("/Users/psksvp/Workspace/infinite");
        //Process p = pb.start();
        try
        {
            System.out.println("here we go");
            timer = new Timer(true);
            InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread());
            timer.schedule(interrupter, 10000);
            p.waitFor();

        }
        catch(InterruptedException e)
        {
            // do something to handle the timeout here
            System.out.println("++");

            p.destroy();
            System.out.println("--");
        }
        finally
        {
            timer.cancel();
            Thread.interrupted();
        }


    }  */
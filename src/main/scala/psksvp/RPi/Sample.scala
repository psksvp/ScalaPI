package psksvp.RPi

/**
  * Created by psksvp on 18/12/2015.
  */
object Sample
{
  def runLife(nGeneration:Int):Unit=
  {
    import psksvp.Toy.CA._

    def draw(life:LifeWorld):Unit=
    {
      val index = 0 until 8
      for(r <- index; c <- index)
      {
        life(r, c) match
        {
          case 0 => SenseHAT.display.setPixel(c, r, (10, 10, 10))
          case 1 => SenseHAT.display.setPixel(c, r, (0, 255, 0))
          case 2 => SenseHAT.display.setPixel(c, r, (100, 0, 0))
        }
      }

      SenseHAT.display.update
    }


    val life = new LifeWorld(worldSize = 8, rule = new Conway)
    draw(life)
    Thread.sleep(200)
    for(i <- (0 until nGeneration))
    {
      life.run
      draw(life)
      Thread.sleep(200)
      println("generation " + i)
    }
    SenseHAT.display.drawString("Done", (0, 244, 0))
    SenseHAT.display.clear
  }
}

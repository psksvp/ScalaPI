package psksvp.Toy.CA

/**
  * Created by psksvp on 20/12/2015.
  */
class LifeWorld(worldSize:Int, rule:Rule)
{
  import java.util.Random
  private val RG = new Random()
  private val currentGen = Array.ofDim[Int](worldSize, worldSize)
  private val nextGen = Array.ofDim[Int](worldSize, worldSize)
  private val index = 0 until worldSize
  private val neighborIdx = -1 to 1
  for(r <- index; c <- index)
    currentGen(r)(c) = RG.nextInt(rule.numberOfStates)

  def size=worldSize
  def apply(row:Int, col:Int):Int=
  {
    require(row >= 0 && row < worldSize)
    require(col >= 0 && col < worldSize)
    currentGen(row)(col)
  }

  def update(row:Int, col:Int, value:Int):Unit=
  {
    require(row >= 0 && row < worldSize)
    require(col >= 0 && col < worldSize)
    require(value >= 0 && value < rule.numberOfStates)
    nextGen(row)(col) = value
  }

  def countNeighbors(r:Int, c:Int):Int=
  {
    var neighbors = 0
    for(i <- neighborIdx; j <- neighborIdx)
    {
      if (1 == currentGen((r + j + worldSize) % worldSize)((c + i + worldSize) % worldSize))
        neighbors = neighbors + 1
    }
    neighbors
  }

  def run:Unit=
  {
    rule.evolve(this)

    for(r <- index; c <- index)
      currentGen(r)(c) = nextGen(r)(c)
  }
}

abstract class Rule
{
  def evolve(life:LifeWorld): Unit
  def numberOfStates: Int
}

class BrianBrain extends Rule
{
  override def numberOfStates: Int = 3
  override def evolve(life:LifeWorld): Unit =
  {
    val index = 0 until life.size
    for(r <- index; c <- index)
    {
      life(r, c) match
      {
        case 0 => if(1 == life.countNeighbors(r, c)) life(r, c) = 1
        case 1 => life(r, c) = 2
        case 2 => life(r, c) = 0
      }
    }
  }
}

class Conway extends Rule
{
  override def numberOfStates: Int = 2
  override def evolve(life:LifeWorld): Unit =
  {
    val index = 0 until life.size
    for(r <- index; c <- index)
    {
      val neighbors = life.countNeighbors(r, c)
      life(r, c) match
      {
        case 0 => if(2 == neighbors) life(r, c) = 1
        case 1 => if(neighbors < 2 || neighbors > 4) life(r, c) = 0
      }
    }
  }
}

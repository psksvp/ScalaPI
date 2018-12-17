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

package psksvp.Math

/**
  * Created by psksvp@gmail.com on 22/4/17.
  */
//http://sce2.umkc.edu/csee/hieberm/281_new/lectures/quine-McCluskey.html
//https://en.wikipedia.org/wiki/Quine–McCluskey_algorithm
object booleanMinimize
{
  type Group = Map[Int, Set[Implicant]]
  type Table = Map[(Implicant, Int), Set[Boolean]]
  /**
    * https://en.wikipedia.org/wiki/Implicant
    * @param terms
    * @param binary
    */
  case class Implicant(terms:Seq[Int], binary:Seq[Char])
  {
    def size:Int = terms.length
    def literalCount:Int = binary.count(_ != '_')
  }

  def vector(length:Int):Vector[Int] = Vector.range(0, length)

  /**
    *
    * @param outputTerms either max terms or min terms
    * @param dontCareTerms
    * @param numberOfVariables
    */
  def apply(outputTerms:Seq[Int],
            dontCareTerms:Seq[Int],
            numberOfVariables:Int):Set[Implicant]=
  {
    val combinedTerms = combineTerms(outputTerms ++ dontCareTerms, numberOfVariables)
    val primeImplicants = primeImplicantTerms(combinedTerms)
    essentialPrimeImplicantTerms(primeImplicants, outputTerms)
  }

  /**
    * // might need this one https://en.wikipedia.org/wiki/Petrick%27s_method
    * @param primeImplicants
    * @param terms
    * @return
    */
  def essentialPrimeImplicantTerms(primeImplicants:Set[Implicant], terms:Seq[Int]):Set[Implicant]=
  {
    /**
      *
      * @param table
      * @return
      */
    def reduceDominances(table: Table): Table =
    {
      /**
        *
        * @param table
        * @return
        */
      def reducedRows(table: Table): Table =
      {
        val (pi, terms) = table.keys.unzip

        /**
          *
          * @param a
          * @param b
          * @return
          */
        def dominating(a:Int, b:Int):Int =
        {
          def computeCoverOfTerm(j:Int, byTerm:Int):(Int, Int) =
          {
            val p = for (t <- pi.toSeq) yield (table(t, j), table(t, byTerm))
            val countJ = p.count(Set(true) == _._1)
            val countK = p.count{ case (x, y) => x == Set(true) && x == y }
            (countJ, countK)
          }

          val (ac, bc) = computeCoverOfTerm(a, byTerm = b)
          if(ac == bc)
            b
          else
          {
            val (bc, ac) = computeCoverOfTerm(b, byTerm = a)
            if(ac == bc)
              a
            else
              -1
          }
        }

        def cover(i:Int, byTerm:Int):Boolean =
        {
          val iVec = for(t <- pi.toIndexedSeq) yield table(t, i)
          val jVec = for(t <- pi.toIndexedSeq) yield table(t, byTerm)
          val rVec = for(i <- jVec.indices) yield iVec(i) & jVec(i)
          iVec == rVec
        }

        val combinations = terms.toSeq.combinations(2)
        val dominators = (for(v <- combinations) yield dominating(v(0), v(1))).filter( _ != -1).toSet
        val rows = for(t <- pi; c <- dominators) yield (t, c)

        table -- rows
      }


      /**
        *
        * @param table
        * @return
        */
      def reduceColumns(table: Table): Table =
      {
        val (pi, terms) = table.keys.unzip

        def dominating(a:Implicant, b:Implicant): Implicant =
        {
          def computeCoverOfImplicant(j:Implicant, byImplicant:Implicant):(Int, Int) =
          {
            val p = for (m <- terms.toSeq) yield (table(j, m), table(byImplicant, m))
            val countA = p.count(Set(true) == _._1)
            val countC = p.count{case (x, y) => x == Set(true) && x == y}
            (countA, countC)
          }

          val (ac1, bc1) = computeCoverOfImplicant(a, byImplicant = b)
          val (bc2, ac2) = computeCoverOfImplicant(b, byImplicant = a)

          if(ac1 == bc1 && bc2 == ac2) // co-covering
          {
            if(a.literalCount > b.literalCount)
              b
            else
              a
          }
          else if(ac1 == bc1)
            b
          else if(ac2 == bc2)
            a
          else
            Implicant(Seq(), Seq())
        }

        val combinations = pi.toSeq.combinations(2)
        val dominators = (for(v <- combinations) yield dominating(v(0), v(1))).filter( _.size > 0).toSet
        val cols = for(m <- terms; t <- dominators) yield (t, m)

        table -- cols
      }
      reduceColumns(reducedRows(table))
    }

    /**
      *
      * @param table
      * @return
      */
    def reduceEssentialImplicants(table: Table): (Set[Implicant], Table) =
    {
      lazy val (pi, terms) = table.keySet.unzip

      lazy val essentialImplicants: Set[Implicant] =
      {
        def implicantsOfMinTerm(min: Int): Set[Implicant] =
        {
          for (t <- pi if Set(true) == table((t, min))) yield t
        }

        def run(ls: List[Int]): Set[Implicant] = ls match
        {
          case Nil       => Set()
          case m :: rest => val t = implicantsOfMinTerm(m)
                            if (1 == t.size)
                              t ++ run(rest)
                            else
                              run(rest)
        }

        val et = run(terms.toList)
        if(et.isEmpty) // NOTE: Pick one randomly for now if empty or pick the one on the list
          Set(pi.toIndexedSeq(scala.util.Random.nextInt(pi.size)))
        else
          et
      }

      lazy val reducedTable: Table =
      {
        val rowMarks = for (m <- terms; t <- essentialImplicants if Set(true) == table((t, m))) yield m
        val row = for (m <- rowMarks; i <- pi) yield (i, m)
        val col = for (t <- essentialImplicants; m <- terms) yield (t, m)
        table -- col -- row
      }

      (essentialImplicants, reducedTable)
    }

    def fixedPointRun(table:Table):Set[Implicant]=
    {
      if(table.isEmpty)
        Set()
      else
      {
        val (e, t) = reduceImplicant(table)
        if(t == table)
          e
        else
          e ++ fixedPointRun(t)
      }

    }

    def reduceImplicant(table:Table):(Set[Implicant], Table) =
    {
      val (e, t) = reduceEssentialImplicants(table)
      (e, reduceDominances(t))
    }

    ///////////////////////////
    val initialTable:Table =
    {
      val pairs = for (t <- primeImplicants; m <- terms) yield
                  {
                    if (t.terms.contains(m))
                      (t, m) -> true
                    else
                      (t, m) -> false
                  }
      pairs.groupBy(_._1).map{ case (k, v) => (k, v.map(_._2)) }
    }

    ///////////////////////////
    fixedPointRun(initialTable)
  }

  /**
    *
    * @param lsCombinedTerms
    * @return
    */
  def primeImplicantTerms(lsCombinedTerms:Seq[Group]):Set[Implicant]=
  {
    /**
      *
      * @param t1
      * @param wasCombinedIntoGroup
      * @return
      */
    def check(t1:Implicant, wasCombinedIntoGroup:Group):Boolean =
    {
      /**
        *
        * @param t1
        * @param wasCombinedWith
        * @return
        */
      def check(t1:Implicant, wasCombinedWith:Implicant):Boolean = t1.terms.toSet subsetOf wasCombinedWith.terms.toSet

      //////////////////////////////////////////
      val r = for(s <- wasCombinedIntoGroup.values; t <- s) yield check(t1, wasCombinedWith = t)
      r.reduce(_ | _)
    }

    /**
      *
      * @param group
      * @param checkWithGroup
      * @return
      */
    def primeImplicantOf(group:Group, checkWithGroup:Group):Set[Implicant] =
    {
      val r = for(s <- group.values;
                  t <- s if false == check(t, wasCombinedIntoGroup = checkWithGroup)) yield t

      r.toSet
    }

    /////////////////////////////////////////
    lsCombinedTerms match
    {
      case Nil       => Set()
      case g :: Nil  => g.values.reduce(_ ++ _)
      case g :: rest => primeImplicantOf(g, checkWithGroup = rest.head) ++  primeImplicantTerms(rest)
    }
  }

  /**
    *
    * @param minTerms
    * @param numberOfVariables
    * @return
    */
  def combineTerms(minTerms:Seq[Int],
                   numberOfVariables:Int):Seq[Group]=
  {
    def fixedPointRun(termGroup:Group,
                      order:Int):List[Group]=
    {
      if(termGroup.isEmpty)
        Nil
      else
      {
        val newTermGroup = combineTermIn(termGroup, order)
        termGroup :: fixedPointRun(newTermGroup, order * 2)
      }
    }

    //////////////////////////////////////////
    val termGroup1 = (for(m <- minTerms)
                       yield Implicant(m :: Nil, psksvp.binaryString(m, numberOfVariables).toCharArray)).toSet.groupBy(countOnes(_))

    fixedPointRun(termGroup1, 2).filter(_.isEmpty == false)
  }

  /**
    *
    * @param t
    * @return
    */
  def countOnes(t: Implicant):Int = t.binary.count('1' == _)


  /**
    *
    * @param a
    * @param b
    * @return
    */
  def differences(a:Seq[Char], b:Seq[Char]):(Int, Seq[Char])=
  {
    require(a.length == b.length)
    val diff = vector(a.length).map{i => if(a(i) == b(i)) a(i) else '_'}
    val count = vector(a.length).map{i => if(a(i) == b(i)) 0 else 1}
    (count.sum, diff)
  }

  /**
    * NOTE::: NEED TO CHECK again, I don't want member LIKE (1,2) and (2, 1)
    * @param lsA
    * @param lsB
    * @tparam U
    * @tparam V
    * @return
    */
  def combination[U, V](lsA:Set[U],
                        lsB:Set[V]):Seq[(U, V)] = (for(a <- lsA; b <- lsB) yield (a, b)).toSeq


  /**
    *
    * @param group
    * @return
    */
  def combineTermIn(group:Group, size:Int):Group =
  {
    /**
      *
      * @param pairs
      * @return
      */
    def combine(pairs:Seq[(Implicant, Implicant)]):Seq[Implicant] =
    {
      /**
        *
        * @param t
        * @param withImplicate
        * @return
        */
      def combine(t:Implicant, withImplicate:Implicant):Implicant =
      {
        val (hammingDist, diff) = differences(t.binary, withImplicate.binary)
        if(1 == hammingDist)
          Implicant((t.terms.toList ::: withImplicate.terms.toList).sorted, diff)
        else
          t
      }

      ///////////////////////////////////////
      vector(pairs.length).map{i => combine(pairs(i)._1, withImplicate = pairs(i)._2)}
    }

    ///////////////////////////////////////
    val implicants = group.keySet.flatMap
    {
      key => if(group.isDefinedAt(key + 1))
               combine(combination(group(key), group(key+1)))
             else
               Nil
    }

    implicants.filter(_.terms.length == size).groupBy(countOnes(_))
  }


  /**
    *
    */
  def test():Unit=
  {
    def toImplicant(term:Int, numberOfVariables:Int):Implicant =
    {
      Implicant(Seq(term), psksvp.binaryString(term, numberOfVariables).toCharArray)
    }

    def toImplicants(terms:List[Int], numberOfVariables:Int):List[Implicant] = terms match
    {
      case Nil => Nil
      case t :: rest => toImplicant(t, numberOfVariables) :: toImplicants(rest, numberOfVariables)
    }


    val m1 = psksvp.Math.booleanMinimize(outputTerms = List(4, 8, 10, 11, 12, 15),
                                          dontCareTerms = List(9, 14),
                                          numberOfVariables = 4)


    val m2 = psksvp.Math.booleanMinimize(outputTerms = List(0, 9, 13, 15),
                                          dontCareTerms = List(7, 12),
                                          numberOfVariables = 4)

    val m3 = psksvp.Math.booleanMinimize(outputTerms = List(0, 2, 5, 6, 7, 8, 10, 12, 13, 14, 15),
                                          dontCareTerms = Nil,
                                          numberOfVariables = 4)

    val m4 = psksvp.Math.booleanMinimize(outputTerms = List(2, 5, 6, 11, 12, 14, 15),
                                          dontCareTerms = List(0, 3, 4),
                                          numberOfVariables = 4)

    val m5 = psksvp.Math.booleanMinimize(outputTerms = List(0, 1, 2, 5, 6, 7),
                                         dontCareTerms = Nil,
                                         numberOfVariables = 3)


    val m6 = psksvp.Math.booleanMinimize(outputTerms = List(0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13),
                                          dontCareTerms = Nil,
                                          numberOfVariables = 4)

    val m7 = psksvp.Math.booleanMinimize(outputTerms = List(2, 3, 7, 9, 11, 13),
                                          dontCareTerms = List(1, 10, 15),
                                          numberOfVariables = 4)


    //https://webdocs.cs.ualberta.ca/~amaral/courses/329/webslides/Topic5-QuineMcCluskey/sld007.htm
    val m8 = psksvp.Math.booleanMinimize(outputTerms = List(0, 1, 2, 5, 6, 7, 8, 9, 10, 14),
                                          dontCareTerms = Nil,
                                          numberOfVariables = 4)

    //https://webdocs.cs.ualberta.ca/~amaral/courses/329/webslides/Topic5-QuineMcCluskey/sld096.htm
    val m9 = psksvp.Math.booleanMinimize(outputTerms = List(0, 1, 2, 5, 6, 7),
                                          dontCareTerms = Nil,
                                          numberOfVariables = 3)


    val m91 = psksvp.Math.booleanMinimize(outputTerms = List(0, 1, 4, 5), Nil, 3)
    val m92 = psksvp.Math.booleanMinimize(outputTerms = List(0, 1, 2, 3, 5, 7, 8, 10, 12, 13, 15), Nil, 4)
    val m93 = psksvp.Math.booleanMinimize(outputTerms = List(1, 2, 3), Nil, 2)

//    println(m1)
//    println(m2)
    println(m3)
//    println(m4)
//    println(m5)
//    println(m6)
//    println(m7)
//    println(m8)
//    println(m9)
//    println(m91)
//    println(m92)
//    println(m93)

//    println("abuse")
//
//
//    val terms = List.fill(600)(scala.util.Random.nextInt(8192)).distinct
//    println(terms)
//    println(terms.length)
//    val start = System.currentTimeMillis()
//    val m10 = psksvp.Math.booleanMinimize(terms, Nil, 13)
//    println(System.currentTimeMillis() - start)
//    println(m10)
  }
}

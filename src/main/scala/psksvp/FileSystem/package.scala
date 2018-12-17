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

package psksvp

package object FileSystem
{
  import java.io.File
  /**
    *
    * @param code
    * @param fileExt
    * @return
    */
  def toFile(code:String, fileExt:String = ".c"):String=
  {
    import java.io.PrintWriter
    var tmpDir = System.getProperty("java.io.tmpdir")
    if(tmpDir.last != '/') tmpDir = tmpDir + "/"
    val file = scala.util.Random.alphanumeric.take(10).mkString
    val fileName = tmpDir + file + fileExt
    new PrintWriter(fileName)
    {
      write(code)
      close()
    }
    fileName
  }

  def fromFile(path:String):String = readString(path)

  def copyFile(path:String, toDir:String):Int =
  {
    import sys.process._
    Seq("cp", path, s"$toDir/.").!
  }

  def makeDirectory(path:String):Int=
  {
    import sys.process._
    Seq("mkdir", "-p", path).!
  }

  def fileExists(path:String):Boolean = new java.io.File(path).exists()

  def writeString(s:String, toFileAtPath:String):Unit=
  {
    import java.io.PrintWriter
    new PrintWriter(toFileAtPath)
    {
      write(s)
      close()
    }
  }

  def readString(fromFileAtPath:String):String =
  {
    import sys.process._
    Seq("cat", fromFileAtPath).!!
  }

  //https://alvinalexander.com/scala/how-to-list-files-in-directory-filter-names-scala
  def listFiles(dir: File, extensions: List[String]): List[File] =
  {
    dir.listFiles.filter(_.isFile).toList.filter
    {
      file => extensions.exists(file.getName.endsWith(_))
    }
  }

  def seqToString[T](ls:Seq[T], separator:String=" "):String =
  {
    if(ls.isEmpty) ""
    else s"${ls.head.toString} ${seqToString(ls.tail, separator)}"
  }

  lazy val home:String = System.getProperty("user.home")
}

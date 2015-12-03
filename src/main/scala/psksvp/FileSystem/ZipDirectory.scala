/**
The BSD 3-Clause License
 Copyright (c) 2015, Pongsak Suvanpong (psksvp@gmail.com)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without
 specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  **/
package psksvp.FileSystem

import java.io.{FileOutputStream, FileInputStream, File}
import java.util.zip.{ZipEntry, ZipOutputStream}

/**
 * Created by psksvp on 27/10/15.
 */
object ZipDirectory
{
  def apply(directoryToZip:String, outputZipFilePath:String):Unit=
  {
    val listOfFiles = ListFiles(new File(directoryToZip))
    writeZipFile(new File(directoryToZip), listOfFiles, outputZipFilePath)
  }

  def writeZipFile(directoryToZip:File, listOfFiles:List[File], outputZipFilePath:String):Unit=
  {
    def appendZipStream(directoryToZip:File, aFile:File, zos:ZipOutputStream):Unit=
    {
      val fis = new FileInputStream(aFile)
      val zipFilePath = aFile.getCanonicalPath.substring(directoryToZip.getCanonicalPath().length() + 1,
                                                          aFile.getCanonicalPath().length())
      zos.putNextEntry(new ZipEntry(zipFilePath))
      val bytes = new Array[Byte](1024)
      var length:Int = fis.read(bytes)
      while (length >= 0)
      {
        zos.write(bytes, 0, length);
        length = fis.read(bytes)
      }

      zos.closeEntry();
      fis.close();
    }

    val fos = new FileOutputStream(outputZipFilePath + ".zip")
    val zos = new ZipOutputStream(fos)
    for(file <- listOfFiles)
    {
      if(false == file.isDirectory)
        appendZipStream(directoryToZip, file, zos)
    }
    zos.close()
    fos.close()
  }
}

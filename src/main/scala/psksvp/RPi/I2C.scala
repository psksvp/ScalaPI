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
package psksvp.RPi

/**
  * Created by psksvp on 11/12/2015.
  */
class I2C(endPointAddress:Int)
{
  import psksvp.jni.rpi.PiI2C
  val fd = PiI2C.wiringPiI2CSetup(endPointAddress)

  def read=PiI2C.wiringPiI2CRead(fd).toByte
  def write(data:Byte)=PiI2C.wiringPiI2CWrite(fd, data.toInt)

  def read(reg:Int)=PiI2C.wiringPiI2CReadReg8(fd, reg).toByte
  def write(reg:Int, data:Byte)=PiI2C.wiringPiI2CWriteReg8(fd, reg, data.toInt)
}

object I2C
{
  private var init=false
  try
  {
    if(false == init)
    {
      import psksvp.FileSystem.SimpleFileIO
      SimpleFileIO.loadNativeLibraryFromJar("/native/libPiI2C.so")
      init = true
    }
  }
  catch
  {
    case e:UnsatisfiedLinkError => sys.error("Native code library failed to load.\n" + e)
  }

  def connect(endPointAddress:Int) = new I2C(endPointAddress)
}

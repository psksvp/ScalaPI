#!/bin/sh
curl https://dl.bintray.com/sbt/debian/sbt-0.13.9.deb > sbt-0.13.9.deb
curl http://downloads.lightbend.com/scala/2.11.8/scala-2.11.8.deb
sudo dpkg -i sbt-0.13.9.deb 
sudo dpkg -i scala-2.11.8.deb

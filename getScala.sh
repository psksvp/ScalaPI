#!/bin/sh
curl https://dl.bintray.com/sbt/debian/sbt-0.13.9.deb > sbt-0.13.9.deb
wget -c http://downloads.typesafe.com/scala/2.11.7/scala-2.11.7.deb?_ga=1.154127736.1386774362.1448663611 > scala-2.11.7.deb
sudo dpkg -i sbt-0.13.9.deb 
sudo dpkg -i scala-2.11.7.deb

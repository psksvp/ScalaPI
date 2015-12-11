/* File : PiGPIO.i */
%module PiGPIO

%{
#include "/usr/include/wiringPi.h"
%}

/* Let's just grab the original header file here */
%include "/usr/include/wiringPi.h"
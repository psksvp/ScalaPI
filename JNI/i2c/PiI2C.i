/* File : wiringI2C.i */
%module PiI2C

%{
#include "/usr/include/wiringPiI2C.h"
%}

/* Let's just grab the original header file here */
%include "/usr/include/wiringPiI2C.h"

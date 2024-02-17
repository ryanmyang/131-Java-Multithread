#!/bin/bash
rm ./Pigzj.gz
rm ./syserr
input=/Users/ryanmyang/Documents/School/CS\ 131/JavaHW/modules
javac *.java
java Pigzj <"${input}" >Pigzj.gz 2>syserr
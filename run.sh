#!/bin/bash

LC_ALL=C
CP=bin:jar/mysql-connector-java-5.1.20-bin.jar:jar/JFlex.jar:jar/ark-1.0-SNAPSHOT.jar:jar/commons-cli-1.2.jar:jar/commons-math3-3.0.jar

echo $1 
echo $2 
echo $3 
echo $4

java -cp ${CP} visualize.Main $1 $2 $3 $4
 

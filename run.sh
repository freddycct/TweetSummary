#!/bin/bash

LC_ALL=C
CP=bin:jar/mysql-connector-java-5.1.20-bin.jar:jar/JFlex.jar:jar/ark-1.0-SNAPSHOT.jar:jar/commons-cli-1.2.jar:jar/commons-math3-3.0.jar

echo arg1:$1 
echo arg2:$2 
echo arg3:$3 
echo arg4:$4

#cat /mnt/freddy/data/tweets/sfbay_facebook_ipo.txt | java -cp ${CP} np_lda.TagTweets > /mnt/freddy/data/tweets/facebook_ipo_init.np
cat /mnt/freddy/data/tweets/facebook_ipo_init.np   | java -cp ${CP} visualize.Main $1 $2 $3 $4


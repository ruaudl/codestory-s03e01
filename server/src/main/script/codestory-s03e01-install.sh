#!/bin/sh
sudo /etc/init.d/codestory-s03e01 stop
sudo cp /home/org-n10/codestory-s03e01.sh /etc/init.d/codestory-s03e01
mv /home/org-n10/codestory-s03e01-server-1.0-SNAPSHOT-jar-with-dependencies.jar /home/org-n10/codestory-s03e01.jar
sudo /etc/init.d/codestory-s03e01 start
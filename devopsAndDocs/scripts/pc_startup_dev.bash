#!/bin/bash

pathogenicityCalculatorFileName=$(ls *.jar)
profile="dev"

nohup java -jar -Dspring.profiles.active=${profile} ${pathogenicityCalculatorFileName} &
if [ $? -eq 0 ]; then
   echo "Starting ${pathogenicityCalculatorFileName} jar, profile: ${profile}"
else
   echo "ERROR: Unable to start ${pathogenicityCalculatorFileName} jar!"
fi

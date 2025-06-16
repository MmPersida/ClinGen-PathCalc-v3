#!/bin/bash

psOutput=$(ps -ef | grep pathogenicity_calculator | grep -v grep)
processID=$(echo ${psOutput} | awk '{ print $2 }')
echo "Process id of pathogenicity_calculator is: ${processID}"

kill -9 ${processID}

if [ $? -eq 0 ]; then
        echo "Pathogenicity Calculator terminated!"
else
        echo "Unable to spot process pathogenicity_calculator, shutdown the device or do it manualy using the ps and kill commands!"
fi


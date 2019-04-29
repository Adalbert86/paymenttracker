#!/bin/bash

mvn clean compile assembly:single
java -jar target/paymenttracker-1.0-jar-with-dependencies.jar sampleInput.txt 

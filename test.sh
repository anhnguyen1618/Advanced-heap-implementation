#!/bin/bash
javac -d ./out -cp . ./src/com/company/*.java && cd out/ && java -cp . com.company.Main
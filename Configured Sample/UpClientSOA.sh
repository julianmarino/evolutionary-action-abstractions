#!/bin/bash

#PBS -N ClientSOA${M}
#PBS -l walltime=336:00:00

cd /mnt/nfs/home/julian/SelectionProject/symetricmaps/populexpanded/A3N_size24_USP_2

RESULTS=/mnt/nfs/home/julian/SelectionProject/symetricmaps/populexpanded/A3N_size24_USP_2/logs/

module load java-oracle/jdk1.8.0_65

#/data/apps/java/jre1.8.0_66/bin/java -Xmx4g -XX:ParallelGCThreads=4 -jar MicroRTS.jar ${FOLDER} ${RESULTS} 

java -Xmx4g -jar MicroRTS.jar ${FOLDER} ${RESULTS} 


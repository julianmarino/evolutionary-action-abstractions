#!/bin/bash

#PBS -N GA_24_2
#PBS -l walltime=336:00:00

cd /mnt/nfs/home/julian/SelectionProject/symetricmaps/populexpanded/A3N_size24_USP_2

module load java-oracle/jdk1.8.0_65

java -Xmx4g -XX:ParallelGCThreads=2 -jar GA_Scale.jar


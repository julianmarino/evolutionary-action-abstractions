#!/bin/bash


cd /storage1/dados/es91661/MicroRTSDadosThread/


(qsub -l nodes=1:ppn=1,mem=1gb aControlMicroRts.sh) &

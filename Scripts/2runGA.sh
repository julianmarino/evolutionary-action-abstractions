#!/bin/bash

cd /storage1/dados/es91661/ExecAIGA

(qsub -l nodes=1:ppn=2,mem=2gb UpGASOA.sh) &




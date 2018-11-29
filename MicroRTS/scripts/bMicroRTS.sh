#!/bin/bash

cd /storage1/dados/es91661/MicroRTSDadosTorn

source /etc/profile.d/modules.sh 

#module load java/jdk1.8.0_101
module load java/jre1.8.0_66

#printf ${IA}" "${MAP}"\n"

java -jar microRTS.jar ${IA} ${MAP}



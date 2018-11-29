#!/bin/bash

cd /storage1/dados/es91661/MicroRTSDadosThread

source /etc/profile.d/modules.sh 

#module load java/jdk1.8.0_101
module load java/jre1.8.0_66

#printf ${IA}" "${MAP}"\n"

# Obtem o pid do shell corrente 
PID_THIS_SHELL=$$
JOB_INFO=`qstat -f $PBS_JOBID`
CPU_LIST=`echo $JOB_INFO | sed -r 's/.*exec_host.=.[[:graph:]]+\/([[:graph:]]+).*/\1/g'`
echo "Running shell pid=$PID_THIS_SHELL"
echo "CPU_LIST=$CPU_LIST"
echo "taskset -cp $CPU_LIST $PID_THIS_SHELL"
#https://linux.die.net/man/1/taskset
taskset -cp $CPU_LIST $PID_THIS_SHELL

java -Xmx4g -jar microRTS.jar ${IA1} ${IA2} ${MAP}



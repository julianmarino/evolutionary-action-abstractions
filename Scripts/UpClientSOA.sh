#!/bin/bash

cd /storage1/dados/es91661/ExecAIGASOA

RESULTS=/storage1/dados/es91661/ExecAIGA/logs

source /etc/profile.d/modules.sh 

module load java/jre1.8.0_66

PID_THIS_SHELL=$$
JOB_INFO=`qstat -f $PBS_JOBID`
CPU_LIST=`echo $JOB_INFO | sed -r 's/.*exec_host.=.[[:graph:]]+\/([[:graph:]]+).*/\1/g'`
echo "Running shell pid=$PID_THIS_SHELL"
echo "CPU_LIST=$CPU_LIST"
echo "taskset -cp $CPU_LIST $PID_THIS_SHELL"
#https://linux.die.net/man/1/taskset
taskset -cp $CPU_LIST $PID_THIS_SHELL

/data/apps/java/jre1.8.0_66/bin/java -Xmx5g -XX:ParallelGCThreads=5 -jar microRTS.jar ${RESULTS} ${FOLDER}



#!/bin/bash

cd /storage1/dados/es91661/MicroRTSDadosThread/


for ((i=0; i<17;i++)) ;  
do
	for ((j=0; j<17;j++)) ; 
	do
		for ((x=0; x<2;x++)) ; 
		do
			if [ "$i" -ne "$j" ];
			then
				(qsub -l nodes=1:ppn=4,mem=4gb -v IA1=$i,IA2=$j,MAP=$x bMicroRTS.sh) &
			fi

			q=$(qselect -u es91661 | wc -l)
        	        while [ "$q" -gt "140" ]
        	        do
        	        	echo "$q"
        	                sleep 3
        	                q=$(qselect -u es91661 | wc -l)
        	        done
		done
	done
done

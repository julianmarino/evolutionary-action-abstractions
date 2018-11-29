
#!/bin/bash
# My first script
echo "Script!"

cd /storage1/dados/es91661/MicroRTSDadosTorn/

for ((i=1; i<7;i++)) ; 
do
	for ((k=1; k<6;k++)) ; 
	do
		(qsub -l nodes=1:ppn=14,mem=3gb -v IA=$i,MAP=$k bMicroRTS.sh) &	
	done
done

#!/bin/bash

total=39

folder="/mnt/nfs/home/julian/SelectionProject/symetricmaps/populexpanded/A3N_size24_USP_2/configSOA/SOA"

for ((m=1; m<=total;m++)) ;  
do
	folder="/mnt/nfs/home/julian/SelectionProject/symetricmaps/populexpanded/A3N_size24_USP_2/configSOA/SOA"$m"/"
	echo $folder
	(qsub -l nodes=1:ppn=4,mem=4gb -v FOLDER=$folder,M=$m UpClientSOA.sh) &
done



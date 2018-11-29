#!/bin/bash

#rodar o total de clientes SOA que serão utilizados.
cd /mnt/nfs/home/julian/SelectionProject/symetricmaps/populexpanded/A3N_size24_USP_2

(qsub -l nodes=1:ppn=2,mem=2gb UpGASOA.sh) &





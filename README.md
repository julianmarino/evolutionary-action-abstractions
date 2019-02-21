# Evolutionary Action-Abstractions.

The framework is divided into three folders:

MicroRTS - It is the microRTS necessary to execute the match that provide de Fitness Function. The user needs to generate the executable jar setting the main class SOARoundRobinTOScale and changing the algorithm that will perform the test. 

Genetic Algorithm - It is the Genetic Algorithm in truth. Here, you can change variables like mutation rate, crossover rate, the number of generations and others. 

Scripts - Here, there are all scripts necessary to run the framework in clusters using torque PBS. 

We advise using the folder Configured Sample as support the initial tests. It is fully configured. To run that example in any torque PBS cluster, you need to run the 1runGA.sh and after that 2runSOA.sh. The first script will run the Genetic Algorithm and the second one will perform the microRTS clients to process all matchs. Please, you need to verify all paths inside of these scripts.

For more informations, please look the article:

Evolving Action Abstractions for Real-Time Planning in Extensive-Form Games
Julian Mariño, Rubens O. Moraes, Claudio Toledo, and Levi Lelis
In the Proceedings of the Conference on Artificial Intelligence (AAAI), 2019.

@inproceedings{MarinoMoraesToledoLelis,
 author    = {Julian Mari{\~{n}}o and
              Rubens Moraes and
               Claudio Toledo
               and Levi Lelis},
 title     = {Evolving Action Abstractionsfor Real-Time Planning in Extensive-Form Games},
 booktitle = {Proceedings of the {AAAI} Conference on Artificial Intelligence},
 year      = {2019},
}

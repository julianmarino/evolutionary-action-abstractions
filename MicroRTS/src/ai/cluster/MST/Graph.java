/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.cluster.MST;

import ai.cluster.core.distance.DistanceCalculator;
import ai.cluster.core.hdbscanstar.UndirectedGraph;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import util.Pair;

/**
 *
 * @author rubens
 */
public class Graph {

    private List<Nodo> graph;

    public Graph() {
        this.graph = new ArrayList<>();
    }

    public UndirectedGraph build(HashMap<GameState, List<PlayerAction>> listActionByState, int playerForThisComputation) {
        graph.clear();
        buildNodosBase(listActionByState.values());

        buildGraph(listActionByState);
        if(graph.isEmpty()){
            return null;
        }
        double[] coreDistances = calculateCoreDistances(2);
        return constructMST(coreDistances, true);
        //return new UndirectedGraph(graph.size(), verticesA, verticesB, edgeWeights);
    }

    public int getTotalNodos(){
        return graph.size();
    }
    
    public ArrayList<Unit> getUnitsOrdered(GameState gs){
        ArrayList<Unit> unitsCl = new ArrayList<>();
        for (Nodo nodo : graph) {
            Unit un = gs.getUnit(nodo.getIdUnidade());
            if(un != null){
                unitsCl.add(un);
            }
        }
        return unitsCl;
    }
    
    public double[][] generateDataSet(GameState gs){
        ArrayList<Unit> unitsCl = new ArrayList<>();
        for (Nodo nodo : graph) {
            Unit un = gs.getUnit(nodo.getIdUnidade());
            if(un != null){
                unitsCl.add(un);
            }
        }
        
        double[][] dataSet = new double[unitsCl.size()][2];
        int idx = 0;
        for (Unit unit : unitsCl) {
            double[] tempPosition = new double[2];
            tempPosition[0] = unit.getX();
            tempPosition[1] = unit.getY();
            //System.out.println(unit.getX()+","+unit.getY());
            dataSet[idx] = tempPosition;
            idx++;
        }
        
        return dataSet;
    }
    
    private void buildNodosBase(Collection<List<PlayerAction>> values) {
        HashSet<Long> ids = new HashSet<>();
        for (List<PlayerAction> value : values) {
            for (PlayerAction playerAction : value) {
                for (Pair<Unit, UnitAction> action : playerAction.getActions()) {
                    ids.add(action.m_a.getID());
                }
            }
        }

        for (long l : ids) {
            graph.add(new Nodo(l));
        }
    }

    private void buildGraph(HashMap<GameState, List<PlayerAction>> listActionByState) {
        for (GameState gameState : listActionByState.keySet()) {
            List<PlayerAction> lPlayer = listActionByState.get(gameState);
            for (PlayerAction playerAction : lPlayer) {
                for (Pair<Unit, UnitAction> action : playerAction.getActions()) {
                    if (action.m_b.getType() == UnitAction.TYPE_ATTACK_LOCATION) {
                        //I need to take the unit position (enemy)
                        Unit enemy = getUnitEnemyByLocation(action.m_b, gameState);
                        if (enemy != null) {
                            addNodeRelation(action, enemy, gameState);
                        }
                    }
                }
            }
        }
    }

    private Unit getUnitEnemyByLocation(UnitAction action, GameState gameState) {
        for (Unit unit : gameState.getUnits()) {
            if (unit.getPlayer() >= 0) {
                if (unit.getX() == action.getLocationX() && unit.getY() == action.getLocationY()) {
                    return unit;
                }
            }
        }

        return null;
    }

    private void addNodeRelation(Pair<Unit, UnitAction> action, Unit enemy, GameState gameState) {
        Unit un = action.m_a;
        UnitAction uAct = action.m_b;
        //get enemy nodo. 
        Nodo searchEn = null;
        Nodo searchAly = null;
        for (Nodo nodo : graph) {
            if (nodo.getIdUnidade() == enemy.getID()) {
                searchEn = nodo;
            }
            if (nodo.getIdUnidade() == un.getID()) {
                searchAly = nodo;
            }
        }
        if(searchEn == null){
            //include new unit
            searchEn = new Nodo(enemy.getID());
            graph.add(searchEn);
        }
        //vinculo nodo
        searchAly.addNodeAdj(un.getAttackRange(), searchEn);
    }

    public void print() {
        for (Nodo nodo : graph) {
            System.out.println("Begin--------");
            nodo.print();
            System.out.println("end --------");
        }
    }

    private UndirectedGraph constructMST(double[] coreDistances, boolean selfEdges) {

        int selfEdgeCapacity = 0;
        if (selfEdges) {
            selfEdgeCapacity = graph.size();
        }

        //One bit is set (true) for each attached point, or unset (false) for unattached points:
        BitSet attachedPoints = new BitSet(graph.size());

        //Each point has a current neighbor point in the tree, and a current nearest distance:
        int[] nearestMRDNeighbors = new int[graph.size() - 1 + selfEdgeCapacity];
        double[] nearestMRDDistances = new double[graph.size() - 1 + selfEdgeCapacity];

        for (int i = 0; i < graph.size() - 1; i++) {
            nearestMRDDistances[i] = Double.MAX_VALUE;
        }

        //The MST is expanded starting with the last point in the data set:
        int currentPoint = graph.size() - 1;
        int numAttachedPoints = 1;
        attachedPoints.set(graph.size() - 1);

        //Continue attaching points to the MST until all points are attached:
        while (numAttachedPoints < graph.size()) {
            int nearestMRDPoint = -1;
            double nearestMRDDistance = Double.MAX_VALUE;

            //Iterate through all unattached points, updating distances using the current point:
            for (int neighbor = 0; neighbor < graph.size(); neighbor++) {
                if (currentPoint == neighbor) {
                    continue;
                }
                if (attachedPoints.get(neighbor) == true) {
                    continue;
                }

                double distance = getEdgeValue(currentPoint, neighbor);

                double mutualReachabiltiyDistance = distance;

                if (coreDistances[currentPoint] > mutualReachabiltiyDistance) {
                    mutualReachabiltiyDistance = coreDistances[currentPoint];
                }
                if (coreDistances[neighbor] > mutualReachabiltiyDistance) {
                    mutualReachabiltiyDistance = coreDistances[neighbor];
                }

                if (mutualReachabiltiyDistance < nearestMRDDistances[neighbor]) {
                    nearestMRDDistances[neighbor] = mutualReachabiltiyDistance;
                    nearestMRDNeighbors[neighbor] = currentPoint;
                }

                //Check if the unattached point being updated is the closest to the tree:
                if (nearestMRDDistances[neighbor] <= nearestMRDDistance) {
                    nearestMRDDistance = nearestMRDDistances[neighbor];
                    nearestMRDPoint = neighbor;
                }
            }

            //Attach the closest point found in this iteration to the tree:
            attachedPoints.set(nearestMRDPoint);
            numAttachedPoints++;
            currentPoint = nearestMRDPoint;

        }

        //Create an array for vertices in the tree that each point attached to:
        int[] otherVertexIndices = new int[graph.size() - 1 + selfEdgeCapacity];
        for (int i = 0; i < graph.size() - 1; i++) {
            otherVertexIndices[i] = i;
        }

        //If necessary, attach self edges:
        if (selfEdges) {
            for (int i = graph.size() - 1; i < graph.size() * 2 - 1; i++) {
                int vertex = i - (graph.size() - 1);
                nearestMRDNeighbors[i] = vertex;
                otherVertexIndices[i] = vertex;
                nearestMRDDistances[i] = coreDistances[vertex];
            }
        }

        return new UndirectedGraph(graph.size(), nearestMRDNeighbors, otherVertexIndices, nearestMRDDistances);
    }

    private double[] calculateCoreDistances( int k) {
        int numNeighbors = k - 1;
        double[] coreDistances = new double[graph.size()];

        if (k == 1) {
            for (int point = 0; point < graph.size(); point++) {
                coreDistances[point] = 0;
            }
            return coreDistances;
        }

        for (int point = 0; point < graph.size(); point++) {
            double[] kNNDistances = new double[numNeighbors];	//Sorted nearest distances found so far
            for (int i = 0; i < numNeighbors; i++) {
                kNNDistances[i] = Double.MAX_VALUE;
            }

            for (int neighbor = 0; neighbor < graph.size(); neighbor++) {
                if (point == neighbor) {
                    continue;
                }
                double distance = getEdgeValue(point, neighbor);

                //Check at which position in the nearest distances the current distance would fit:
                int neighborIndex = numNeighbors;
                while (neighborIndex >= 1 && distance < kNNDistances[neighborIndex - 1]) {
                    neighborIndex--;
                }

                //Shift elements in the array to make room for the current distance:
                if (neighborIndex < numNeighbors) {
                    for (int shiftIndex = numNeighbors - 1; shiftIndex > neighborIndex; shiftIndex--) {
                        kNNDistances[shiftIndex] = kNNDistances[shiftIndex - 1];
                    }
                    kNNDistances[neighborIndex] = distance;
                }
            }
            coreDistances[point] = kNNDistances[numNeighbors - 1];
        }

        return coreDistances;
    }

    private double getEdgeValue(int PosNodoBase, int PosNodoEnemy) {
        long idNodoBase = graph.get(PosNodoBase).getIdUnidade();
        long idNodoEnemy = graph.get(PosNodoEnemy).getIdUnidade();

        for (Nodo nodo : graph) {
            if (nodo.getIdUnidade() == idNodoBase) {
                for (Incidencia inc : nodo.incidencias) {
                    if (inc.nodo.getIdUnidade() == idNodoEnemy) {
                        return inc.edge;
                    }
                }
            }
        }
        
        idNodoBase = graph.get(PosNodoEnemy).getIdUnidade();
        idNodoEnemy = graph.get(PosNodoBase).getIdUnidade();
        try{
        for (Nodo nodo : graph) {
            if (nodo.getIdUnidade() == idNodoBase) {
                for (Incidencia inc : nodo.incidencias) {
                    if (inc.nodo.getIdUnidade() == idNodoEnemy) {
                        return inc.edge;
                    }
                }
            }
        }
        }catch(NullPointerException e ){
            System.out.println("ai.cluster.MST.Graph.getEdgeValue()");
            System.out.println("ai.cluster.MST.Graph.getEdgeValue()"+ e);
        }
        return Double.MAX_VALUE;
    }

}

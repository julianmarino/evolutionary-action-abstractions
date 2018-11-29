/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.cluster.MST;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rubens
 */
public class Nodo {

    private long idUnidade;

    HashSet<Incidencia> incidencias;

    public Nodo() {
        this.incidencias = new HashSet<>();
    }

    public Nodo(long idUnidade) {
        this.idUnidade = idUnidade;
        this.incidencias = new HashSet<>();
    }

    public void addNodeAdj(double edge, Nodo node) {
        this.incidencias.add(new Incidencia(node, edge));
    }
    
    public void updateNodeAdj(double edge, Nodo node){
        if(existIncidencia(node)){
            //if node exist, update edge
            getIncidencia(node).setEdge(edge);
        }else{
            addNodeAdj(edge, node);
        }
    }
    
    private Incidencia getIncidencia(Nodo node){
        for (Incidencia incidencia : incidencias) {
            if(incidencia.nodo.idUnidade == node.idUnidade){
                return incidencia;
            }
        }
        
        return null;
    }
    
    public double getEdgeIncidencia(Nodo node){
        for (Incidencia incidencia : incidencias) {
            if(incidencia.nodo.idUnidade == node.idUnidade){
                return incidencia.edge;
            }
        }
        
        return 0.0;
    }
    
    public boolean existIncidencia(Nodo node){
        for (Incidencia incidencia : incidencias) {
            if(incidencia.nodo.idUnidade == node.idUnidade){
                return true;
            }
        }
        
        return false;
    }
    
    public void print(){        
        System.out.println("Node = "+idUnidade);
        incidencias.forEach((incidencia) -> {
            incidencia.print();
        });
    }
    
    public void printID(){
        System.out.println("Node = "+ idUnidade);
    }

    public long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(long idUnidade) {
        this.idUnidade = idUnidade;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Nodo other = (Nodo) obj;
        if (this.idUnidade != other.idUnidade) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (int) (this.idUnidade ^ (this.idUnidade >>> 32));
        return hash;
    }
    
    
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.cluster.MST;

import java.util.Objects;

/**
 *
 * @author rubens
 */
public class Incidencia {

    Nodo nodo;  // referência ao nodo adjacente
    double edge;   // valor da aresta

    public Incidencia(Nodo nodo, double edge) {
        this.nodo = nodo;
        this.edge = edge;
    }

    public Nodo getNodo() {
        return nodo;
    }

    public void setNodo(Nodo nodo) {
        this.nodo = nodo;
    }

    public double getEdge() {
        return edge;
    }

    public void setEdge(double edge) {
        this.edge = edge;
    }
    
    public void print(){
        System.out.println("   Edge ="+ edge);
        System.out.println("   Nodo ="+ nodo.getIdUnidade());
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
        final Incidencia other = (Incidencia) obj;
        if (this.edge != other.edge) {
            return false;
        }
        if (!Objects.equals(this.nodo, other.nodo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.nodo);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.edge) ^ (Double.doubleToLongBits(this.edge) >>> 32));
        return hash;
    }

    
    
    
}

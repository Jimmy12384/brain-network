package com.jfennelly.brain.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Neurons represent a static point within a {@link NeuronGraph}. Neurons are used both
 * for rendering, connecting to other neurons, and acting as the highway for {@link NeuronOrb}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Neuron {
    private final List<Neuron> connectedNeurons = new ArrayList<>();
    private float x;
    private float y;
    private float z;

    /**
     * Adds a connection to a node if the node has less than four existing connections
     */
    public void addAdjacent(Neuron node) {
        if (connectedNeurons.size() < 4) {
            connectedNeurons.add(node);
        }
    }

    @Override
    public String toString() {
        return "{" + this.x + ", " + this.y + ", " + this.z + "}";
    }

    /**
     * calculates the secant distance between two 3D points.
     */
    public double calculateDistance(Neuron comp) {
        return Math.sqrt(Math.pow(this.x - comp.getX(), 2) + Math.pow(this.y - comp.getY(), 2) + Math.pow(this.z - comp.getZ(), 2));
    }
}

package com.jfennelly.brain.network;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Neuron Graph represents the entire brain network. Contains the entire list of
 * {@link Neuron}, and builds the network based on positional data of the nodes to associate
 * {@link Neuron}'s to each other.
 */
@Data
public class NeuronGraph {
    private static final double MIN_NEURON_DISTANCE = 5.0;
    private static final double MAX_NEURON_DISTANCE = 25;
    private List<Neuron> neurons;

    /**
     * <p>
     * Initializes the network graph from a raw set of unconnected neurons. The neurons
     * are immediately processed using a scanning technique to locally search and prune
     * neurons that are too close from each other.
     * </p>
     * <br/>
     * <p>
     * The scanning technique only processes nodes within a range the size of max neuron distance. each iteration, the scanning
     * range is iterated by half of the scanning distance to account for nodes between
     * previous scanning ranges.
     * </p>
     * <br/>
     * TODO: this can be further optimized by initializing nodes as a quad tree instead of scanning
     */
    public NeuronGraph(List<Neuron> neurons) {
        this.neurons = neurons;
        neurons.sort(Comparator.comparing(Neuron::getX));

        //forms edges amongst nodes depending on the distance between nodes and minimizes nodes with too small distance
        int maxIterations = (int) (4 * Math.ceil(this.neurons.get(this.neurons.size() - 1).getX() / MAX_NEURON_DISTANCE));
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double minBounds = this.neurons.get(0).getX() + ((MAX_NEURON_DISTANCE * iteration) / 2);
            List<Neuron> neuronsInRange = getAllNodesInScanningRange(minBounds);
            initializeNeurons(neuronsInRange);
        }

        List<Neuron> lonelyNeurons = this.neurons.stream().filter(neuron -> neuron.getConnectedNeurons().size() == 0).toList();
        this.neurons.removeAll(lonelyNeurons);
    }

    private List<Neuron> getAllNodesInScanningRange(double minBounds) {
        return neurons.stream().filter(neuron -> neuron.getX() >= minBounds && neuron.getX() <= minBounds + MAX_NEURON_DISTANCE).collect(Collectors.toList());
    }

    /**
     * Removes all neurons where the distance from an existing neuron is less than {@code MIN_NEURON_DISTANCE}
     * <br/><br/>
     * For all valid neurons where the local distance is between {@code MIN_NEURON_DISTANCE} and {@code MAX_NEURON_DISTANCE}, the neurons are linked as adjacent.
     */
    private void initializeNeurons(List<Neuron> neuronsScanner) {
        for (int i = 0; i < neuronsScanner.size(); i++) {
            for (int j = 0; j < neuronsScanner.size(); j++) {
                if (neuronsScanner.get(i).calculateDistance(neuronsScanner.get(j)) < MIN_NEURON_DISTANCE && i != j) {
                    neurons.remove(neuronsScanner.get(i));
                    break;
                } else if (i != j && neuronsScanner.get(i).calculateDistance(neuronsScanner.get(j)) < MAX_NEURON_DISTANCE) {
                    neuronsScanner.get(i).addAdjacent(neuronsScanner.get(j));
                    neuronsScanner.get(j).addAdjacent(neuronsScanner.get(i));
                }
            }
        }
    }
}

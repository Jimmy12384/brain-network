package com.jfennelly.brain.network;

import com.jfennelly.brain.network.util.RandomUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Neuron Orbs represent electrical signals moving between {@link Neuron}. Neuron
 * Orbs are responsible for managing their own positional data over time, including
 * selection of which {@link Neuron}'s to move towards.
 */
@Data
public class NeuronOrb {
    private static final int MAX_TRAIL = 25;
    private final int size;
    private final float velocity = (float) RandomUtils.randInt(4, 10) / 10;
    private final List<float[]> trail;
    private float[] color;
    private int distanceTraveled;
    private boolean markedForDeletion;
    private float x;
    private float y;
    private float z;
    private Neuron nextNeuron;
    private Neuron previousNeuron;

    /**
     * Initializes a Neuron orb by assigning a position based on the associated neuron.
     * Automatically selects a new neuron to walk to based on connected neurons.
     */
    public NeuronOrb(Neuron neuron) {
        this.markedForDeletion = false;
        this.distanceTraveled = 0;
        this.previousNeuron = new Neuron();

        this.size = 7;
        this.x = neuron.getX();
        this.y = neuron.getY();
        this.z = neuron.getZ();
        this.color = new float[]{this.x, this.y, this.z};

        List<Neuron> connectedNeurons = neuron.getConnectedNeurons();
        if (connectedNeurons.size() > 0) {
            Neuron randomConnectedNeuron = RandomUtils.getRandomItem(connectedNeurons);
            this.walkTowards(randomConnectedNeuron);
        }
        trail = new ArrayList<>();
    }

    /**
     * Selects a new neuron to walk towards each frame.
     */
    public void walkTowards(Neuron neuron) {
        this.previousNeuron = this.nextNeuron;
        this.nextNeuron = neuron;
        this.distanceTraveled++;
    }

    /**
     *  Updates a neuron Orb for the next frame. updates the trail to contain the
     *  previous position, moves the orb towards the next neuron by the set velocity,
     *  and selects a new neuron if the next neuron has been approached.
     */
    public void iterate() {
        if (nextNeuron != null) {
            trail.add(new float[]{this.x, this.y, this.z});
            this.color = new float[]{this.x, this.y, this.z};
            if (trail.size() > MAX_TRAIL) {
                trail.remove(0);
            }

            this.x += (nextNeuron.getX() > this.x ? 1 : -1) * velocity;
            this.y += (nextNeuron.getY() > this.y ? 1 : -1) * velocity;
            this.z += (nextNeuron.getZ() > this.z ? 1 : -1) * velocity;

            if (hasApproachedNeuron()) {
                Neuron nextNeuron = RandomUtils.getRandomItemNonRepeat(this.nextNeuron.getConnectedNeurons(), this.previousNeuron);
                this.walkTowards(nextNeuron);
            }
        }
    }

    /**
     * An orb has approached the next neuron if the distance between all the
     * components are less than the velocity of the object.
     */
    private boolean hasApproachedNeuron() {
        return Math.abs(nextNeuron.getX() - this.x) <= velocity &&
                Math.abs(nextNeuron.getY() - this.y) <= velocity &&
                Math.abs(nextNeuron.getZ() - this.z) <= velocity;
    }
}

package com.jfennelly.brain.network;

import com.jfennelly.brain.network.utils.RandomUtils;
import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Main runner for the brain network application. Responsible for integrating
 * all actions related to physically rendering the scene.
 */
public class BrainNetworkRunner extends PApplet {
    private static final int ORB_RATE = 2;
    private static final int MAX_NODE_WALK_DISTANCE = 30;
    private static float time = 0;
    private static float angle;
    private static boolean paused = false;
    private PShape brainModel;
    private NeuronGraph brain;
    private List<NeuronOrb> orbs;

    public static void main(String[] args) {
        PApplet.main("com.jfennelly.brain.network.BrainNetworkRunner", args);
    }

    /**
     * Processing settings function. primarily used for changing processing related settings to set up scene generation.
     */
    public void settings() {
        size(1920, 1080, P3D);
    }

    /**
     * Processing setup function. Automatically runs before first frame render to set up the scene.
     */
    public void setup() {
        brainModel = loadShape("models/face.obj");
        angle = 0;
        orbs = new ArrayList<>();

        List<Neuron> neurons = new ArrayList<>();
        for (int i = 0; i < brainModel.getChildCount(); i += 50) {
            PShape child = brainModel.getChild(i);
            PVector v = child.getVertex(0);
            neurons.add(new Neuron(v.x * 280, (v.y * 280 - 170), v.z * 280));
        }
        brain = new NeuronGraph(neurons);

        for (int i = 0; i < neurons.size(); i++) {
            if (RandomUtils.randInt(0, ORB_RATE) == (i % ORB_RATE + 1)) {
                orbs.add(new NeuronOrb(neurons.get(i)));
            }
        }
    }

    /**
     * Processing draw function. Automatically runs every frame to determine the next render.
     */
    public void draw() {
        float HALF_SCREEN_WIDTH = (float) width / 2;
        float HALF_SCREEN_HEIGHT = (float) height / 2;

        //initiate world settings for each frame
        if (!paused) {
            translate(HALF_SCREEN_WIDTH, HALF_SCREEN_HEIGHT - 105);
            pointLight(255, 0, 0, HALF_SCREEN_WIDTH, HALF_SCREEN_HEIGHT, 400);
            lights();
            background(45, 45, 45);
            brain.getNeurons().forEach(this::drawNeuron);
            for (NeuronOrb orb : orbs) {
                if (orb.getDistanceTraveled() > MAX_NODE_WALK_DISTANCE) {
                    orb.setMarkedForDeletion(true);
                }
                drawOrb(orb);
                orb.iterate();
            }

            List<NeuronOrb> deletedOrbs = orbs.stream().filter(NeuronOrb::isMarkedForDeletion).toList();
            spawnOrbs(deletedOrbs.size());
            orbs.removeAll(deletedOrbs);

            //update global variables in respect to time
            angle += 0.006;
            time += 0.1;
        }
    }

    private void renderSourceModel() {
        // I want this to be a class attribute, but processing refuses to update it
        float HALF_SCREEN_WIDTH = (float) width / 2;
        float HALF_SCREEN_HEIGHT = (float) height / 2;

        pushMatrix();
        translate(HALF_SCREEN_WIDTH, HALF_SCREEN_HEIGHT - 100);
        rotateY(-angle);
        rotateX(-PI);
        background(32, 32, 32);
        scale(275);
        shape(brainModel, 0, (float) -0.6);
        popMatrix();
    }

    private void spawnOrbs(int newOrbs) {
        for (int i = 0; i < newOrbs; i++) {
            orbs.add(new NeuronOrb(brain.getNeurons().get(RandomUtils.randInt(0, brain.getNeurons().size() - 1))));
        }
    }

    /**
     * On Key Pressed listener function. on key press, toggle the pause switch for the animation.
     */
    public void keyPressed() {
        paused = !paused;
    }

    private void drawOrb(NeuronOrb orb) {
        float maxZ = map((float) -(orb.getZ() * Math.cos(BrainNetworkRunner.angle)), -150, 0, 0, 100);
        pushMatrix();
        rotateX(-PI);
        rotateY(BrainNetworkRunner.angle);
        strokeWeight(orb.getSize());
        float[] c = orb.getColor();
        stroke(c[0], c[1], c[2], maxZ);
        point(orb.getX(), orb.getY(), orb.getZ() + noise(orb.getX(), orb.getY(), orb.getZ()));
        drawOrbTrail(orb, maxZ, c);
        popMatrix();
    }

    private void drawOrbTrail(NeuronOrb orb, float maxZ, float[] c) {
        List<float[]> trail = orb.getTrail();
        for (int i = 0; i < trail.size(); i++) {
            float x = trail.get(i)[0];
            float y = trail.get(i)[1];
            float z = trail.get(i)[2];

            pushMatrix();
            float trailDecay = (float) 1 / orb.getTrail().size();
            stroke(c[0], c[1], c[2], maxZ * (trailDecay * i));
            strokeWeight(orb.getSize() * (trailDecay * i));
            point(x, y, z);
            popMatrix();
        }
    }

    private void drawNeuron(Neuron neuron) {
        float maxZ = map((float) -(neuron.getZ() * Math.cos(BrainNetworkRunner.angle)), -150, 100, 0, 100);
        pushMatrix();
        rotateX(-PI);
        rotateY(BrainNetworkRunner.angle);
        fill(0, 255, 0);
        stroke(getNodeColor(neuron.getX()), getNodeColor(neuron.getY()), getNodeColor(neuron.getZ()), maxZ);
        stroke(0, maxZ);
        strokeWeight(6);
        point(neuron.getX(), neuron.getY(), neuron.getZ());
        strokeWeight(2);
        for (Neuron adjacent : neuron.getConnectedNeurons()) {
            line(neuron.getX(), neuron.getY(), neuron.getZ(), adjacent.getX(), adjacent.getY(), adjacent.getZ());
        }
        popMatrix();
    }

    private static float getNodeColor(float position) {
        return position * tan(time) % 255;
    }
}

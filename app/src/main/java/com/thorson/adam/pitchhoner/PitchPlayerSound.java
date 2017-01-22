package com.thorson.adam.pitchhoner;

import static com.thorson.adam.pitchhoner.PitchPlayerConstants.*;

import java.util.ArrayList;

/**
 * Created by tor on 1/22/17.
 */
public class PitchPlayerSound {
    private double frequency;
    private int primaryShape;
    private ArrayList<Integer> features;
    private double noise;


    // Constructors
    public PitchPlayerSound(double frequency, int primaryShape, ArrayList<Integer> features,
                            double noise){
        this.frequency = frequency;
        this.primaryShape = primaryShape;
        this.features = features;
        this.noise = noise;
        if(noise > PITCHPLAYER_NOISE_MAX){ this.noise = PITCHPLAYER_NOISE_MAX; }
        else if(noise < 0){ this.noise = 0; }
    }

    public PitchPlayerSound(double frequency, int primaryShape, double noise){
        this(frequency, primaryShape, new ArrayList<Integer>(), noise);
    }

    public PitchPlayerSound(double frequency, int primaryShape){
        this(frequency, primaryShape, 0);
    }

    public PitchPlayerSound(double frequency){ this(frequency, PITCHPLAYER_TONE_PURE); }

    // Methods/functions
    public double getFrequency(){ return frequency; }

    public double getNoise(){ return noise; }

    public int getPrimaryShape(){ return primaryShape; }

    public ArrayList<Integer> getFeatures(){ return new ArrayList<Integer>(features); }

    public void addFeature(int feature){ features.add(feature); }

    public void addFeatures(ArrayList<Integer> features){
        for(Integer feature : features){ this.features.add(feature); }
    }
}

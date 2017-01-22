package com.thorson.adam.pitchhoner;

/**
 * Created by tor on 1/14/17.
 */
public class PitchPlayerConstants {
    // Sampling
    public static final double PITCHPLAYER_SAMPLE_HZ = 44100;
    public static final int BITS_PER_SAMPLE = 16;
    public static final int BYTES_PER_SAMPLE = BITS_PER_SAMPLE / 8;
    // Tones
    // Primary Shape
    public static final int PITCHPLAYER_TONE_PURE = 0;
    public static final int PITCHPLAYER_TONE_SAW = 1;
    public static final int PITCHPLAYER_TONE_SQUARE = 2;
    // Secondary Shape
    public static final int PITCHPLAYER_TONE_SECONDARY_OSCILLATION = 0;
    // Oscillation
    public static final int PITCHPLAYER_OSCILLATION_DEFAULT = 5;
    // Noise
    public static final double PITCHPLAYER_NOISE_DEFAULT = 0.1;
    public static final double PITCHPLAYER_NOISE_MAX = 0.25;
    // Frequency
    // Notes
    public static final int PITCHPLAYER_C = 0;
    public static final int PITCHPLAYER_C_SHARP = 1;
    public static final int PITCHPLAYER_D = 2;
    public static final int PITCHPLAYER_D_SHARP = 3;
    public static final int PITCHPLAYER_E = 4;
    public static final int PITCHPLAYER_F = 5;
    public static final int PITCHPLAYER_F_SHARP = 6;
    public static final int PITCHPLAYER_G = 7;
    public static final int PITCHPLAYER_G_SHARP = 8;
    public static final int PITCHPLAYER_A = 9;
    public static final int PITCHPLAYER_A_SHARP = 10;
    public static final int PITCHPLAYER_B = 11;
    public static final double[] PITCHPLAYER_NOTES = new double[]{
            261.6, 277.2, 293.7, 311.1, 329.6, 349.2, 370.0, 392.0, 415.3, 440.0, 466.2, 493.9
    };
    public static final int PITCHPLAYER_NOTE_COUNT = PITCHPLAYER_NOTES.length;
    // Octave
    public static final int PITCHPLAYER_INDEX_OCTAVE_MIDDLE = 4;
}

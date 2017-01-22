package com.thorson.adam.pitchhoner;

import static com.thorson.adam.pitchhoner.PitchPlayerConstants.*;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by tor on 1/14/17.
 */
public class PitchPlayer {
    private static final String TAG = PitchPlayer.class.getSimpleName();

    private Context context;
    private AudioTrack audioTrack;
    private boolean play = false;


    // Constructors
    public PitchPlayer(Context context){
        this.context = context;
    }

    // Methods/functions
    private double calculateNoteToFrequency(int noteIndex, int octaveIndex){
        double frequency = PITCHPLAYER_NOTES[noteIndex];
        double exponent = (double)(octaveIndex - PITCHPLAYER_INDEX_OCTAVE_MIDDLE);
        return (frequency * Math.pow(2.0, exponent));
    }

    private int calculatedSampleByteCount(PitchPlayerSound pitchPlayerSound){
        double frequency = pitchPlayerSound.getFrequency();
        double oscillationFactor = 1;
        /* NEED to fix this
        for(Integer feature : pitchPlayerSound.getFeatures()){
            if(feature == PITCHPLAYER_TONE_SECONDARY_OSCILLATION){
                oscillationFactor = PITCHPLAYER_OSCILLATION_DEFAULT;
            }
        }
        */
        int reducedFractionDenominator = Helpers.computeReducedFraction(
                frequency * oscillationFactor)[1];

        int byteCount = (
                reducedFractionDenominator * ((int)PITCHPLAYER_SAMPLE_HZ) * BYTES_PER_SAMPLE);
        Log.d(TAG, String.format("Reduced fraction denominator %d\nbyte count %d",
                reducedFractionDenominator, byteCount));
        return byteCount;
    }

    private double[] getToneSamples(int sampleCount, PitchPlayerSound pitchPlayerSound){
        double frequency = pitchPlayerSound.getFrequency();
        double noise = pitchPlayerSound.getNoise();
        double[] samples = new double[sampleCount];

        for(int i=0; i < sampleCount; ++i){
            // Primary shape
            // Defaulted to PURE
            double percentage = i / (PITCHPLAYER_SAMPLE_HZ / frequency);
            samples[i] = Math.sin(2 * Math.PI * percentage);
            switch(pitchPlayerSound.getPrimaryShape()){
                case PITCHPLAYER_TONE_PURE:
                    break;
                case PITCHPLAYER_TONE_SAW:
                    double portion = percentage - ((double)((int)percentage));
                    samples[i] = 1.0 - 2.0 * portion;
                    break;
                case PITCHPLAYER_TONE_SQUARE:
                    samples[i] = (samples[i] >= 0) ? 1 : 0;
                    break;
            }

            // Secondary shape
            for(Integer feature : pitchPlayerSound.getFeatures()){
                if(feature == PITCHPLAYER_TONE_SECONDARY_OSCILLATION){
                    double toneFactor = Math.abs(
                            Math.sin(2 * Math.PI * percentage /
                                    ((double) PITCHPLAYER_OSCILLATION_DEFAULT)));
                    samples[i] = toneFactor * samples[i];
                }
            }

            if(noise > 0){
                samples[i] = (1.0 - noise) * samples[i] + noise * Math.random();
            }
        }

        return samples;
    }

    private byte[] generateFrequency(PitchPlayerSound pitchPlayerSound){
        int byteCount = calculatedSampleByteCount(pitchPlayerSound);
        int sampleCount = byteCount / BYTES_PER_SAMPLE;
        double[] samples = getToneSamples(sampleCount, pitchPlayerSound);
        byte[] sound = new byte[byteCount];
        int sampleIndex = 0;
        short sSample;

        for (int i = 0; i < sampleCount; ++i) {
            // Convert to 16 bit; assumes buffer is normalized
            // Maximum signed 16-bit, amplitude
            sSample = (short) ((samples[i] * 32767.0));
            // First byte is lower order
            sound[sampleIndex++] = (byte)(sSample & 0x00ff);
            sound[sampleIndex++] = (byte)((sSample & 0xff00) >> 8);
        }

        return sound;
    }

    public void stop(){
        audioTrack.stop();
        play = false;
    }

    public boolean playSound(PitchPlayerSound pitchPlayerSound){
        Log.d(TAG, String.format("Play frequency %.2f Hz toneId %d",
                pitchPlayerSound.getFrequency(), pitchPlayerSound.getPrimaryShape()));
        byte[] sound = generateFrequency(pitchPlayerSound);
        int sampleCount = sound.length / BYTES_PER_SAMPLE;

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                ((int)PITCHPLAYER_SAMPLE_HZ), AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, sound.length, AudioTrack.MODE_STATIC);

        audioTrack.write(sound, 0, sound.length);
        // Continuous loop (i.e. -1)
        audioTrack.setLoopPoints(0, sampleCount, -1);

        play = true;
        audioTrack.play();

        return true;
    }

    public boolean playFrequency(double frequency, int toneId, boolean hasNoise){
        double noise = (hasNoise ? PITCHPLAYER_NOISE_DEFAULT : 0);
        PitchPlayerSound pitchPlayerSound = new PitchPlayerSound(frequency, toneId, noise);

        return playSound(pitchPlayerSound);
    }

    public boolean playFrequency(double frequency){
        return playFrequency(frequency, PITCHPLAYER_TONE_PURE, false);
    }

    public boolean playNote(int noteIndex, int octaveIndex, int toneId){
        if((noteIndex < 0) || (noteIndex >= PITCHPLAYER_NOTE_COUNT)){
            Log.e(TAG, String.format("Invalid note index %d", noteIndex));
            return false;
        }

        return playFrequency(calculateNoteToFrequency(noteIndex, octaveIndex), toneId, false);
    }

    public boolean playNote(int noteIndex, int octaveIndex){
        return playNote(noteIndex, octaveIndex, PITCHPLAYER_TONE_PURE);
    }

    public boolean playNote(int noteIndex){
        return playNote(noteIndex, PITCHPLAYER_INDEX_OCTAVE_MIDDLE);
    }
}

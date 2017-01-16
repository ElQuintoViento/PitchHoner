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

    private int calculatedSampleByteCount(double frequency){
        int reducedFractionDenominator = Helpers.computeReducedFraction(frequency)[1];
        int byteCount = (reducedFractionDenominator * ((int)PITCHPLAYER_SAMPLE_HZ));
        Log.d(TAG, String.format("Reduced fraction denominator %d\nbyte count %d",
                reducedFractionDenominator, byteCount));
        return byteCount;
    }

    private byte[] generateFrequency(double frequency, int toneId){
        int byteCount = calculatedSampleByteCount(frequency);
        int sampleCount = byteCount / BYTES_PER_SAMPLE;
        byte[] sound = new byte[byteCount];

        double dSample;
        short sSample;
        int sampleIndex = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < sampleCount; ++i) {
            dSample = Math.sin(2 * Math.PI * i / (PITCHPLAYER_SAMPLE_HZ / frequency));
            stringBuilder.append(String.format("%.4f,", dSample));
            // Convert to 16 bit; assumes buffer is normalized
            // Maximum signed 16-bit, amplitude
            sSample = (short) ((dSample * 32767.0));
            // First byte is lower order
            sound[sampleIndex++] = (byte)(sSample & 0x00ff);
            sound[sampleIndex++] = (byte)((sSample & 0xff00) >> 8);
        }

        Log.d(TAG, stringBuilder.toString());

        return sound;
    }

    public void stop(){
        audioTrack.stop();
        play = false;
    }

    public boolean playFrequency(double frequency, int toneId){
        Log.d(TAG, String.format("Play frequency %.2f Hz toneId %d", frequency, toneId));
        byte[] sound = generateFrequency(frequency, toneId);
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

    public boolean playFrequency(double frequency){
        return playFrequency(frequency, PITCHPLAYER_TONE_PURE);
    }

    public boolean playNote(int noteIndex, int octaveIndex, int toneId){
        if((noteIndex < 0) || (noteIndex >= PITCHPLAYER_NOTE_COUNT)){
            Log.e(TAG, String.format("Invalid note index %d", noteIndex));
            return false;
        }

        return playFrequency(calculateNoteToFrequency(noteIndex, octaveIndex), toneId);
    }

    public boolean playNote(int noteIndex, int octaveIndex){
        return playNote(noteIndex, octaveIndex, PITCHPLAYER_TONE_PURE);
    }

    public boolean playNote(int noteIndex){
        return playNote(noteIndex, PITCHPLAYER_INDEX_OCTAVE_MIDDLE);
    }
}

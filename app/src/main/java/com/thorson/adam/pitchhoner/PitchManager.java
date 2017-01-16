package com.thorson.adam.pitchhoner;

import static com.thorson.adam.pitchhoner.PitchManagerConstants.*;
import static com.thorson.adam.pitchhoner.PitchPlayerConstants.*;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by tor on 1/11/17.
 */
public class PitchManager {
    private static final String TAG = PitchManager.class.getSimpleName();

    public interface PitchManagerCallback{
        public void onPitchManagerStatus(int code, int typeId);
    }

    private Context context;
    private Handler mainHandler;
    private Handler pitchHandler;
    private HandlerThread pitchHandlerThread;
    private PitchManagerCallback callback;
    private PitchPlayer pitchPlayer;
    private boolean debug = false;
    private boolean play = false;


    // Constructors
    public PitchManager(Context context, boolean debug){
        this.context = context;
        this.debug = debug;
        this.callback = (PitchManagerCallback) context;
    }

    public PitchManager(Context context){
        this(context, false);
    }

    // Methods/functions
    private void initHandlers(){
        if(mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        if((pitchHandlerThread == null) || (pitchHandler == null)) {
            pitchHandlerThread = new HandlerThread(TAG + System.currentTimeMillis());
            pitchHandlerThread.start();
            pitchHandler = new Handler(pitchHandlerThread.getLooper());
        }
    }

    private void deinitHandlers(){
        pitchHandlerThread.interrupt();
        pitchHandlerThread = null;
        pitchHandler = null;
        mainHandler = null;
    }

    private void initPitchPlayer(){
        if(pitchPlayer == null){
            pitchPlayer = new PitchPlayer(context);
        }
    }

    private void mainPost(Runnable runnable){ mainHandler.post(runnable); }

    private void pitchManagerPost(Runnable runnable){ pitchHandler.post(runnable); }

    private void playFrequency(){
        initPitchPlayer();
        pitchPlayer.playNote(PITCHPLAYER_F);
    }

    public void start(int typeId){
        initHandlers();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                playFrequency();
            }
        };
        pitchManagerPost(runnable);
    }

    public void stop(){
        play = false;
        deinitHandlers();
    }

    private void postStatus(final int code, final int typeId){
        Runnable runnable = new Runnable() {
            @Override
            public void run() { callback.onPitchManagerStatus(code, typeId);}
        };
        mainPost(runnable);
    }
}


package com.zxt.dlna.dmr;

import java.net.URI;
import java.util.logging.Logger;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.greenrobot.eventbus.EventBus;

import com.zxt.dlna.util.Action;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import byron.dlna.NativeAsyncEvent;

/**
 * @author offbye
 */
public class ZxtMediaPlayer {

    final private static Logger log = Logger.getLogger(ZxtMediaPlayer.class.getName());

    private static final String TAG = "GstMediaPlayer";

    final private UnsignedIntegerFourBytes instanceId;
    final private LastChange avTransportLastChange;
    final private LastChange renderingControlLastChange;
    private volatile TransportInfo currentTransportInfo = new TransportInfo();
    private final PositionInfo currentPositionInfo = new PositionInfo();
    private final MediaInfo currentMediaInfo = new MediaInfo();
    private double storedVolume;
    
    private final Context mContext;

    public ZxtMediaPlayer(UnsignedIntegerFourBytes instanceId,Context context,
                          LastChange avTransportLastChange,
                          LastChange renderingControlLastChange) {
        super();
        this.instanceId = instanceId;
        this.mContext = context;
        this.avTransportLastChange = avTransportLastChange;
        this.renderingControlLastChange = renderingControlLastChange;
    }

    public UnsignedIntegerFourBytes getInstanceId() {
        return instanceId;
    }

    public LastChange getAvTransportLastChange() {
        return avTransportLastChange;
    }

    public LastChange getRenderingControlLastChange() {
        return renderingControlLastChange;
    }

    synchronized public TransportInfo getCurrentTransportInfo() {
        return currentTransportInfo;
    }

    synchronized public PositionInfo getCurrentPositionInfo() {
        return currentPositionInfo;
    }

    synchronized public MediaInfo getCurrentMediaInfo() {
        return currentMediaInfo;
    }

    synchronized public void setURI(URI uri, String type, String name, String currentURIMetaData) {
        Log.i(TAG, "setURI " + uri);
        NativeAsyncEvent event = new NativeAsyncEvent(type, String.valueOf(uri), name);
        EventBus.getDefault().post(event);
    }

    synchronized public void setVolume(double volume) {
        Log.i(TAG,"setVolume " + volume);
    }

    synchronized public void setMute(boolean desiredMute) {

    }

    synchronized public TransportAction[] getCurrentTransportActions() {
        TransportState state = currentTransportInfo.getCurrentTransportState();
        TransportAction[] actions;

        switch (state) {
            case STOPPED:
                actions = new TransportAction[]{
                        TransportAction.Play
                };
                break;
            case PLAYING:
                actions = new TransportAction[]{
                        TransportAction.Stop,
                        TransportAction.Pause,
                        TransportAction.Seek
                };
                break;
            case PAUSED_PLAYBACK:
                actions = new TransportAction[]{
                        TransportAction.Stop,
                        TransportAction.Pause,
                        TransportAction.Seek,
                        TransportAction.Play
                };
                break;
            default:
                actions = null;
        }
        return actions;
    }

    synchronized protected void transportStateChanged(TransportState newState) {
        TransportState currentTransportState = currentTransportInfo.getCurrentTransportState();
        log.fine("Current state is: " + currentTransportState + ", changing to new state: " + newState);
        currentTransportInfo = new TransportInfo(newState);

        getAvTransportLastChange().setEventedValue(
                getInstanceId(),
                new AVTransportVariable.TransportState(newState),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions())
        );
    }

    public double getVolume() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        double v =  (double)audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "getVolume " + v);
        return v;
    }
    
    public void play() {
        Log.i(TAG,"play");
        sendBroadcastAction(Action.PLAY);
    }

    public void pause() {
        Log.i(TAG,"pause");
        sendBroadcastAction(Action.PAUSE);
    }

    public void stop() {
        Log.i(TAG,"stop");
        sendBroadcastAction(Action.STOP);
    }
    
    public void seek(int position) {
        Log.i(TAG,"seek " +  position);
        Intent intent = new Intent();
        intent.setAction(Action.DMR);
        intent.putExtra("helpAction", Action.SEEK);
        intent.putExtra("position", position);
        mContext.sendBroadcast(intent);
    }
    
    public void sendBroadcastAction(String action) {
        Intent intent = new Intent();
        intent.setAction(Action.DMR);
        intent.putExtra("helpAction", action);
        mContext.sendBroadcast(intent);
    }
}


package byron.dlna;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
public class RNByronVlcView extends TextureView implements LifecycleEventListener, TextureView.SurfaceTextureListener {
    private final RCTEventEmitter mEventEmitter;
    private String mSrc;
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer;
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean mPaused = false;
    private ArrayList<String> vlcOptions = new ArrayList<>();

    public RNByronVlcView(ThemedReactContext context) {
        super(context);
        mEventEmitter = context.getJSModule(RCTEventEmitter.class);
        context.addLifecycleEventListener(this);
        setSurfaceTextureListener(this);
        addOnLayoutChangeListener(onLayoutChangeListener);
    }

    /*************
     * Events  Listener
     *************/
    private final View.OnLayoutChangeListener onLayoutChangeListener = new View.OnLayoutChangeListener(){
        @Override
        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
            if(view.getWidth() > 0 && view.getHeight() > 0 ){
                mWidth = view.getWidth(); // 获取宽度
                mHeight = view.getHeight(); // 获取高度
                if(mMediaPlayer != null) {
                    IVLCVout vlcOut = mMediaPlayer.getVLCVout();
                    vlcOut.setWindowSize(mWidth, mHeight);
                }
            }
        }
    };

    /**
     * 播放过程中的时间事件监听
     */
    private final MediaPlayer.EventListener mPlayerListener = new MediaPlayer.EventListener(){
        @Override
        public void onEvent(MediaPlayer.Event event) {
            WritableMap eventMap = Arguments.createMap();
            eventMap.putDouble("currentTime", mMediaPlayer.getTime());
            eventMap.putDouble("duration", mMediaPlayer.getLength());
            eventMap.putDouble("position", mMediaPlayer.getPosition());
            switch (event.type) {
                case MediaPlayer.Event.Buffering:
                case MediaPlayer.Event.EncounteredError:
                case MediaPlayer.Event.EndReached:
                case MediaPlayer.Event.Playing:
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.PositionChanged:
                case MediaPlayer.Event.Stopped:
                    eventMap.putInt("type", event.type);
                    mEventEmitter.receiveEvent(getId(), "onEventVlc", eventMap);
                    break;
            }
        }
    };

    private final IVLCVout.OnNewVideoLayoutListener onNewVideoLayoutListener = new IVLCVout.OnNewVideoLayoutListener(){
        @Override
        public void onNewVideoLayout(IVLCVout vout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

        }
    };

    IVLCVout.Callback callback = new IVLCVout.Callback() {
        @Override
        public void onSurfacesCreated(IVLCVout ivlcVout) {

        }

        @Override
        public void onSurfacesDestroyed(IVLCVout ivlcVout) {

        }
    };

    private void releasePlayer() {
        if (libvlc == null) {
            return;
        }
        mMediaPlayer.stop();
        mMediaPlayer.setEventListener(null);
        libvlc = null;
    }

    private void createPlayer() {
        if(this.getSurfaceTexture() == null) {
            return;
        }
        releasePlayer();
        try {
            libvlc = new LibVLC(getContext(), vlcOptions);
            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            IVLCVout vlcOut =  mMediaPlayer.getVLCVout();
            vlcOut.setWindowSize(mWidth, mHeight);
            Media media = new Media(libvlc, Uri.parse(mSrc));
            mMediaPlayer.setMedia(media);
            if (!vlcOut.areViewsAttached()) {
                vlcOut.addCallback(callback);
                vlcOut.setVideoSurface(this.getSurfaceTexture());
                vlcOut.attachViews(onNewVideoLayoutListener);
            }
            if (!mPaused) mMediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  视频进度调整
     */
    public void setTime(int time) {
        if(mMediaPlayer != null){
            mMediaPlayer.setTime(time);
        }
    }

    /**
     * 设置资源路径
     */
    public void setSrc(String uri) {
        mSrc = uri;
        createPlayer();
    }

    public void setOptions(ReadableArray options) {
        vlcOptions = new ArrayList<>();
        ArrayList<Object> mOptions = options.toArrayList();
        for(Object obj : mOptions){
            vlcOptions.add("" + obj);
        }
    }

    /**
     * 改变播放速率
     */
    public void setRate(float rate) {
        if(mMediaPlayer != null){
            mMediaPlayer.setRate(rate);
        }
    }

    /**
     * 改变播放状态
     */
    public void setPaused(boolean paused){
        mPaused = paused;
        if(mMediaPlayer != null){
            if(paused){
                mMediaPlayer.pause();
            }else{
                mMediaPlayer.play();
            }
        }
    }

    /**
     * 改变宽高比
     */
    public void setAspectRatio(String aspectRatio){
        if(mMediaPlayer != null){
            mMediaPlayer.setAspectRatio(aspectRatio);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        mWidth = width;
        mHeight = height;
        createPlayer();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        releasePlayer();
    }

    public void cleanUpResources() {
        removeOnLayoutChangeListener(onLayoutChangeListener);
        releasePlayer();
    }
}

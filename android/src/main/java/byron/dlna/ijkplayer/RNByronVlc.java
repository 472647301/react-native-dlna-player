package byron.dlna.ijkplayer;

import android.annotation.SuppressLint;
import android.media.audiofx.AudioEffect;
import android.os.Handler;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

@SuppressLint("ViewConstructor")
public class RNByronVlc extends FrameLayout implements LifecycleEventListener, onTimedTextAvailable, AudioEffect.OnEnableStatusChangeListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener {

    public RCTEventEmitter mEventEmitter;
    public boolean mPlayInBackground = true;
    private final ThemedReactContext themedContext;
    private IjkVideoView mVideoView;
    private boolean mPaused = false;
    private boolean mMuted = false;
    private float mVolume = 1.0f;
    private final float mPlaybackSpeed = 1.0f;
    private final boolean mRepeat = false;
    private boolean mLoaded = false;
    private boolean mStalled = false;
    private String mVideoSource;
    private String mUserAgent;
    private ReadableMap mHeaders;

    private final float mProgressUpdateInterval = 1000;
    private final Handler mProgressUpdateHandler = new Handler();
    private Runnable mProgressUpdateRunnable = null;
    private OnAudioSessionIdRecieved mAudioSessionIdListener;

    public RNByronVlc(ThemedReactContext themedReactContext) {
        super(themedReactContext);

        themedContext = themedReactContext;
        loadNativeJNI();
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        setFocusable(false);
        setFocusableInTouchMode(false);
        initializePlayer();

    }

    /**
     * Initialize the player
     * <p>
     * Attach VideoView
     * Add Progress update event
     * Attach all event listeners for the video player
     */
    public void initializePlayer() {
        setVideoView();
        setProgressUpdateRunnable();
        setEventListeners();
    }

    /**
     * Release the player
     */
    private void releasePlayer() {
        if (mVideoView == null) return;

        final IjkVideoView videoView = mVideoView;
        mProgressUpdateRunnable = null;
        mVideoView = null;

        videoView.setOnPreparedListener(null);
        videoView.setOnErrorListener(null);
        videoView.setOnCompletionListener(null);
        videoView.setOnInfoListener(null);
        videoView.setOnBufferingUpdateListener(null);
        videoView.setOnTimedTextAvailableListener(null);
        videoView.stopPlayback();
        removeView(videoView);
        videoView.release(true);
    }

    /**
     * Set the video view and attach it to the screen.
     */
    private void setVideoView() {
        if (themedContext != null) {
            mVideoView = new IjkVideoView(themedContext);
            addView(mVideoView);
            mVideoView.setContext(themedContext, getId());
            mVideoView.setFocusable(false);
            mVideoView.setFocusableInTouchMode(false);
            setupLayoutHack();
        }
    }

    /**
     * Load ijkplayer native lib
     */
    private void loadNativeJNI() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    /**
     * Set event listeners for the video player
     */
    private void setEventListeners() {
        if (themedContext != null && mVideoView != null) {
            mEventEmitter = themedContext.getJSModule(RCTEventEmitter.class);
            themedContext.addLifecycleEventListener(this);
            mVideoView.setOnTimedTextAvailableListener(this);
            mVideoView.setOnPreparedListener(this);
            mVideoView.setOnErrorListener(this);
            mVideoView.setOnCompletionListener(this);
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
        }
    }

    /**
     * Set the progressUpdateInterval
     */
    private void setProgressUpdateRunnable() {
        if (mVideoView != null)
            mProgressUpdateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mVideoView != null && mVideoView.isPlaying() && !mPaused) {
                        WritableMap event = Arguments.createMap();
                        event.putDouble(Constants.EVENT_PROP_CURRENT_TIME, mVideoView.getCurrentPosition());
                        event.putDouble(Constants.EVENT_PROP_DURATION, mVideoView.getDuration());
                        mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_PROGRESS.toString(), event);

                        mProgressUpdateHandler.postDelayed(mProgressUpdateRunnable, Math.round(mProgressUpdateInterval));
                    }
                }
            };

    }

    /**
     * Set Video Source
     *
     * @param uriString   Local path or url to the video
     * @param readableMap Headers for the url if any
     * @param userAgent   Adds a userAgent
     */
    public void setSrc(final String uriString, @Nullable final ReadableMap readableMap, @Nullable final String userAgent) {
        if (uriString == null)
            return;

        mLoaded = false;
        mStalled = false;
        mVideoSource = uriString;
        mHeaders = readableMap;
        mUserAgent = userAgent;

        if (mVideoView.isPlaying()) {
            releasePlayer();
            WritableMap event = Arguments.createMap();
            mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_SWITCH.toString(), event);
        }
        if (mVideoView == null) {
            initializePlayer();
        }

        if (userAgent != null && mVideoView != null)
            mVideoView.setUserAgent(userAgent);

        if (readableMap != null) {
            Map<String, String> headerMap = new HashMap<>();
            ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
            StringBuilder headers = new StringBuilder();
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                ReadableType type = readableMap.getType(key);
                if (type == ReadableType.String) {
                    headers
                            .append(key)
                            .append(": ")
                            .append(readableMap.getString(key))
                            .append("\r\n");
                }
            }
            headerMap.put("Headers", headers.toString());
            mVideoView.setVideoPath(uriString, headerMap);
        } else {
            assert mVideoView != null;
            mVideoView.setVideoPath(uriString);
        }
    }

    /**
     * Play or pause the video
     *
     * @param paused pause video if true
     */
    public void setPausedModifier(final boolean paused) {
        mPaused = paused;
        if (mVideoView == null) return;
        if (mPaused) {
            mVideoView.pause();
        } else {
            mVideoView.start();
            mProgressUpdateHandler.post(mProgressUpdateRunnable);
        }
    }

    /**
     * Seek the video to given time in ms
     *
     * @param seekTime       seek time in ms
     * @param pauseAfterSeek Should the video pause after seek has completed
     */
    public void setSeekModifier(final double seekTime, final boolean pauseAfterSeek) {
        if (mVideoView != null)
            mVideoView.seekTo((int) (seekTime * mVideoView.getDuration()));
    }

    /**
     * Mute the audio
     *
     * @param muted
     */
    public void setMutedModifier(final boolean muted) {
        mMuted = muted;
        if (mVideoView == null) return;
        if (mMuted) {
            mVideoView.setVolume(0.0f, 0.0f);
        } else {
            mVideoView.setVolume(mVolume, mVolume);
        }
    }

    /**
     * Set the volume of the video, independant from Android system volume.
     *
     * @param volume
     */
    public void setVolumeModifier(final float volume) {
        mVolume = volume;
        if (mVideoView != null) {
            if (volume > 1.0f || volume < 0.0f) return;
            mVideoView.setVolume(mVolume, mVolume);

        }
    }

    public void setAudio(final boolean audioEnabled) {
        if (mVideoView == null) return;
        mVideoView.setAudio(audioEnabled);

    }

    public void setVideo(final boolean videoEnabled) {
        if (mVideoView == null) return;
        mVideoView.setVideo(videoEnabled);

    }

    public void applyModifiers() {
        setPausedModifier(mPaused);
        setMutedModifier(mMuted);
    }

    @Override
    public void onEnableStatusChange(AudioEffect effect, boolean enabled) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mPlayInBackground) {
            releasePlayer();
            initializePlayer();
            setSrc(mVideoSource, mHeaders, mUserAgent);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releasePlayer();
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        WritableMap event = Arguments.createMap();
        event.putDouble(Constants.EVENT_PROP_CURRENT_TIME, mVideoView.getDuration());
        event.putDouble(Constants.EVENT_PROP_DURATION, mVideoView.getDuration());
        mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_PROGRESS.toString(), event);
        mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_END.toString(), null);
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int frameworkErr, int implErr) {
        WritableMap event = Arguments.createMap();
        event.putInt("code", frameworkErr);
        mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_ERROR.toString(), event);
        releasePlayer();
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int message, int val) {

        switch (message) {
            case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                setMutedModifier(true);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setMutedModifier(false);
                        setVolumeModifier(1);
                    }
                }, 2000);
                mStalled = true;
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                setMutedModifier(false);
                mStalled = false;
                break;
            case IjkMediaPlayer.MEDIA_INFO_AUDIO_SEEK_RENDERING_START:
            case IjkMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START:
            case IjkMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                handlePlayPauseOnVideo();
                break;
        }
        return true;
    }

    private void handlePlayPauseOnVideo() {
        if (mPaused) {
            setMutedModifier(false);
            mVideoView.pause();
            WritableMap event = Arguments.createMap();
            event.putBoolean("paused", true);
            mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_PAUSED.toString(), event);
        } else {
            setMutedModifier(false);
            WritableMap event = Arguments.createMap();
            event.putBoolean("paused", false);
            mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_PAUSED.toString(), event);
        }

    }

    public void setOnAudioSessionIdListener(OnAudioSessionIdRecieved audioSessionIdListener) {
        mAudioSessionIdListener = audioSessionIdListener;
    }


    @Override
    public void onTimedText(String subtitle) {

    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        if (mLoaded)
            return;
        if (mAudioSessionIdListener != null)
            mAudioSessionIdListener.onAudioSessionId(iMediaPlayer);

        WritableMap event = Arguments.createMap();
        event.putDouble(Constants.EVENT_PROP_DURATION, mVideoView.getDuration());
        event.putDouble(Constants.EVENT_PROP_CURRENT_TIME, mVideoView.getCurrentPosition());
        mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_START.toString(), event);
        mLoaded = true;
        applyModifiers();
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        if (!mStalled)
            return;
        WritableMap event = Arguments.createMap();
        mEventEmitter.receiveEvent(getId(), Constants.Events.EVENT_BUFFER.toString(), event);
    }

    @Override
    public void onHostPause() {
        if (!mPlayInBackground) {
            mVideoView.pause();
        }
    }

    @Override
    public void onHostResume() {
        if (!mPlayInBackground && !mPaused) {
            setPausedModifier(false);

        }
    }

    @Override
    public void onHostDestroy() {
        releasePlayer();
    }

    void setupLayoutHack() {

        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                manuallyLayoutChildren();
                getViewTreeObserver().dispatchOnGlobalLayout();
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }

    void manuallyLayoutChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        }
    }
}

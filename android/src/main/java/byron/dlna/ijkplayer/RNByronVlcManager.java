package byron.dlna.ijkplayer;


import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.io.IOException;
import java.util.Map;

public class RNByronVlcManager extends ViewGroupManager<RNByronVlc> {

    public static final String PROP_SRC = "src";
    public static final String PROP_SRC_HEADERS = "headers";
    public static final String PROP_SRC_URI = "uri";
    public static final String PROP_SRC_USER_AGENT = "userAgent";
    public static final String PROP_MUTED = "muted";
    public static final String PROP_VIDEO_PAUSED = "paused";
    public static final String PROP_SEEK = "seek";
    public static final String PROP_VOLUME = "volume";
    public static final String PROP_SNAPSHOT_PATH = "snapshotPath";
    public static final String PROP_RESIZE_MODE = "resizeMode";
    private static final String REACT_CLASS = "RNByronVlc";
    private static final String PROP_REPEAT = "repeat";
    private static final String PROP_PROGRESS_UPDATE_INTERVAL = "progressUpdateInterval";
    private static final String PROP_RATE = "rate";
    private static final String PROP_AUDIO_TRACK = "selectedAudioTrack";
    private static final String PROP_VIDEO_TRACK = "selectedVideoTrack";
    private static final String PROP_TEXT_TRACK = "selectedTextTrack";
    private static final String PROP_DESELECT_TRACK = "deselectTrack";
    private static final String PROP_SUBTITLE_DISPLAY = "subtitleStyle";
    private static final String PROP_SUBTITLES = "subtitlesEnabled";
    private static final String PROP_DISABLE_AUDIO = "audioDisabled";
    private static final String PROP_DISABLE_VIDEO = "videoDisabled";
    private static final String PROP_AUDIO_FOCUS = "audioFocus";
    private static final String PROP_PLAY_IN_BACKGROUND = "playInBackground";
    private static final String PROP_EQUALIZER_ENABLED = "equalizerEnabled";
    private static final String PROP_ASYNC_DECODING = "asyncDecoding";
    private static final String PROP_BASSBOOST_ENABLED = "bassBoostEnabled";
    private static final String PROP_LOUDNESS_ENABLED = "loudnessEnhancerEnabled";
    private static final String PROP_REVERB_MODE = "reverbMode";
    private static final String PROP_REVERB_ENABLED = "reverbEnabled";

    private RNByronVlc mRNByronVlc;
    private Equalizer mEqualizer;

    public RNByronVlcManager(ReactApplicationContext reactContext) {
        super();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public RNByronVlc createViewInstance(ThemedReactContext context) {
        mRNByronVlc = new RNByronVlc(context);
        mEqualizer = new Equalizer(mRNByronVlc.getContext());
        mRNByronVlc.setOnAudioSessionIdListener(mEqualizer);

        return mRNByronVlc;

    }


    public RNByronVlc getPlayerInstance() { // <-- returns the View instance
        return mRNByronVlc;
    }

    public Equalizer getEqualizerInstance() {
        if (mEqualizer == null)
            mEqualizer = new Equalizer(mRNByronVlc.getContext());


        return mEqualizer;
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Constants.Events event : Constants.Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }


    @ReactProp(name = PROP_SRC)
    public void setSrc(final RNByronVlc mVideoView, @Nullable ReadableMap src) throws IOException {
        String uri = src.getString(PROP_SRC_URI);
        ReadableMap headers = null;
        String userAgent = "";
        if (src.hasKey(PROP_SRC_HEADERS))
            headers = src.getMap(PROP_SRC_HEADERS);

        if (src.hasKey(PROP_SRC_USER_AGENT))
            userAgent = src.getString(PROP_SRC_USER_AGENT);

        mVideoView.setSrc(uri, headers, userAgent);

    }

    @ReactProp(name = PROP_MUTED, defaultBoolean = false)
    public void setMuted(final RNByronVlc mVideoView, final boolean muted) {
        mVideoView.setMutedModifier(muted);
    }

    @ReactProp(name = PROP_SEEK, defaultDouble = 0.0)
    public void setSeek(final RNByronVlc mVideoView, final double seekTime) {

        mVideoView.setSeekModifier(seekTime, false);

    }

    @ReactProp(name = PROP_SNAPSHOT_PATH)
    public void setSnapshotPath(final RNByronVlc mVideoView, final String snapshotPath) throws IOException {
        mVideoView.setSnapshotPath(snapshotPath);
    }

    @ReactProp(name = PROP_VIDEO_PAUSED, defaultBoolean = false)
    public void setPaused(final RNByronVlc mVideoView, final boolean paused) {
        mVideoView.setPausedModifier(paused);
    }

    @ReactProp(name = PROP_RESIZE_MODE)
    public void setResizeMode(final RNByronVlc mVideoView, final String resizeMode) {

        mVideoView.setResizeModifier(resizeMode);
    }

    @ReactProp(name = PROP_VOLUME, defaultFloat = 1.0f)
    public void setVolume(final RNByronVlc mVideoView, final float volume) {
        mVideoView.setVolumeModifier(volume);
    }

    @ReactProp(name = PROP_REPEAT, defaultBoolean = false)
    public void setRepeat(final RNByronVlc mVideoView, final boolean repeat) {
        mVideoView.setRepeatModifer(repeat);
    }

    @ReactProp(name = PROP_RATE, defaultFloat = 1.0f)
    public void setPlaybackRate(final RNByronVlc mVideoView, final float rate) {
        mVideoView.setPlaybackRateModifer(rate);
    }

    @ReactProp(name = PROP_PROGRESS_UPDATE_INTERVAL, defaultInt = 250)
    public void setProgressUpdateInterval(final RNByronVlc mVideoView, final int progressUpdateInterval) {
        mVideoView.setProgressUpdateInterval(progressUpdateInterval);
    }

    @ReactProp(name = PROP_AUDIO_TRACK)
    public void selectAudioTrack(final RNByronVlc mVideoView, final int trackID) {
        mVideoView.selectAudioTrack(trackID);
    }

    @ReactProp(name = PROP_VIDEO_TRACK)
    public void selectVideoTrack(final RNByronVlc mVideoView, final int trackID) {
        mVideoView.selectVideoTrack(trackID);
    }

    @ReactProp(name = PROP_TEXT_TRACK)
    public void selectTextTrack(final RNByronVlc mVideoView, final int trackID) {
        mVideoView.selectTextTrack(trackID);

    }

    @ReactProp(name = PROP_DESELECT_TRACK)
    public void deselectTrack(final RNByronVlc mVideoView, final int trackID) {
        mVideoView.deselectTrack(trackID);

    }


    @ReactProp(name = PROP_SUBTITLE_DISPLAY)
    public void setSubtitleDisplay(final RNByronVlc mVideoView, ReadableMap subtitleStyle) {

        String color = subtitleStyle.getString("color");
        String backgroundColor = subtitleStyle.getString("backgroundColor");
        int textSize = subtitleStyle.getInt("textSize");
        String position = subtitleStyle.getString("position");

        mVideoView.setSubtitleDisplay(textSize, color, position, backgroundColor);
    }

    @ReactProp(name = PROP_SUBTITLES, defaultBoolean = false)
    public void setSubtitle(final RNByronVlc mVideoView, final boolean subtitlesEnabled) {
        mVideoView.setSubtitles(subtitlesEnabled);
    }

    @ReactProp(name = PROP_DISABLE_AUDIO, defaultBoolean = false)
    public void setAudio(final RNByronVlc mVideoView, final boolean audioDisabled) {
        mVideoView.setAudio(audioDisabled);
    }

    @ReactProp(name = PROP_DISABLE_VIDEO, defaultBoolean = false)
    public void setVideo(final RNByronVlc mVideoView, final boolean videoDisabled) {
        mVideoView.setVideo(videoDisabled);
    }

    @ReactProp(name = PROP_AUDIO_FOCUS, defaultBoolean = true)
    public void setAudioFocus(final RNByronVlc mVideoView, final boolean audioFocus) {

        mVideoView.setAudioFocus(audioFocus);
    }


    @ReactProp(name = PROP_PLAY_IN_BACKGROUND, defaultBoolean = false)
    public void setBackgroundPlay(final RNByronVlc mVideoView, final boolean playInBackground) {

        mVideoView.setBackgroundPlay(playInBackground);
    }

    @ReactProp(name = PROP_EQUALIZER_ENABLED, defaultBoolean = false)
    public void setEqualizerEnabled(final RNByronVlc mVideoView, final boolean equalizerEnabled) {
        if (mEqualizer == null) {
            mEqualizer = new Equalizer(mRNByronVlc.getContext());
        }

        mEqualizer.setEqualizerEnabled(equalizerEnabled);

    }

    @ReactProp(name = PROP_ASYNC_DECODING, defaultBoolean = false)
    public void setAsyncDecoding(final RNByronVlc mVideoView, final boolean asyncDecoding) {

        // TODO


    }

    @ReactProp(name = PROP_BASSBOOST_ENABLED, defaultBoolean = false)
    public void setBassBoostEnabled(final RNByronVlc mVideoView, final boolean enabled) {
        if (mEqualizer == null) {
            mEqualizer = new Equalizer(mRNByronVlc.getContext());
        }


        mEqualizer.setBassBoostEnabled(enabled);

    }

    @ReactProp(name = PROP_LOUDNESS_ENABLED, defaultBoolean = false)
    public void setLoudnessEnabled(final RNByronVlc mVideoView, final boolean enabled) {
        if (mEqualizer == null)
            mEqualizer = new Equalizer(mRNByronVlc.getContext());
        mEqualizer.setLoudnessEnabled(enabled);

    }

    @ReactProp(name = PROP_REVERB_ENABLED, defaultBoolean = false)
    public void setReverbEnabled(final RNByronVlc mVideoView, final boolean enabled) {
        if (mEqualizer == null)
            mEqualizer = new Equalizer(mRNByronVlc.getContext());
        //mEqualizer.setReverbEnabled(enabled);

    }

    @ReactProp(name = PROP_REVERB_MODE)
    public void setReverbMode(final RNByronVlc mVideoView, final String reverbMode) {
        if (mEqualizer == null)
            mEqualizer = new Equalizer(mRNByronVlc.getContext());
        // mEqualizer.setPresetReverbMode(reverbMode);
    }


}

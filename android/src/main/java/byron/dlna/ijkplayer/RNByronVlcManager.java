package byron.dlna.ijkplayer;


import android.util.Log;

import androidx.annotation.NonNull;
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
    private static final String REACT_CLASS = "RNByronVlc";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    public RNByronVlc createViewInstance(@NonNull ThemedReactContext context) {
        RNByronVlc mRNByronVlc = new RNByronVlc(context);
        Equalizer mEqualizer = new Equalizer(mRNByronVlc.getContext());
        mRNByronVlc.setOnAudioSessionIdListener(mEqualizer);

        return mRNByronVlc;

    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        for (Constants.Events event : Constants.Events.values()) {
            Log.d(REACT_CLASS, event.toString());
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @ReactProp(name = PROP_SRC)
    public void setSrc(final RNByronVlc mVideoView, @Nullable ReadableMap src) throws IOException {
        assert src != null;
        String uri = src.getString(PROP_SRC_URI);
        ReadableMap headers = null;
        String userAgent = "";
        if (src.hasKey(PROP_SRC_HEADERS)) {
            headers = src.getMap(PROP_SRC_HEADERS);
        }

        if (src.hasKey(PROP_SRC_USER_AGENT)) {
            userAgent = src.getString(PROP_SRC_USER_AGENT);
        }

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

    @ReactProp(name = PROP_VIDEO_PAUSED, defaultBoolean = false)
    public void setPaused(final RNByronVlc mVideoView, final boolean paused) {
        mVideoView.setPausedModifier(paused);
    }

    @ReactProp(name = PROP_VOLUME, defaultFloat = 1.0f)
    public void setVolume(final RNByronVlc mVideoView, final float volume) {
        mVideoView.setVolumeModifier(volume);
    }
}

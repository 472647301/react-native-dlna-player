package byron.dlna;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nullable;

public class RNByronVlcManager extends SimpleViewManager<RNByronVlcView> {

    @NonNull
    @Override
    public String getName() {
        return "RNByronVlc";
    }

    @NonNull
    @Override
    protected RNByronVlcView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new RNByronVlcView(reactContext);
    }

    @Override
    public void onDropViewInstance(@NonNull RNByronVlcView view) {
        view.cleanUpResources();
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
        builder.put("onEventVlc", MapBuilder.of("registrationName", "onEventVlc"));
        return builder.build();
    }

    @ReactProp(name = "source")
    public void setSource(final RNByronVlcView playerView, ReadableMap map) {
        String path = map.getString("uri");
        ReadableArray options = map.getArray("options");
        if (TextUtils.isEmpty(path)) {
            return;
        }
        assert options != null;
        playerView.setOptions(options);
        playerView.setSrc(path);
    }

    @ReactProp(name = "time")
    public void setTime(final RNByronVlcView playerView, int time) {
        playerView.setTime(time);
    }

    @ReactProp(name = "rate")
    public void setRate(final RNByronVlcView playerView, float rate) {
        playerView.setRate(rate);
    }

    @ReactProp(name = "paused")
    public void setPaused(final RNByronVlcView playerView, boolean paused) {
        playerView.setPaused(paused);
    }

    @ReactProp(name = "aspectRatio")
    public void setAspectRatio(final RNByronVlcView playerView, String aspectRatio) {
        playerView.setAspectRatio(aspectRatio);
    }
}
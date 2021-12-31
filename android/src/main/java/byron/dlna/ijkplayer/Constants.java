package byron.dlna.ijkplayer;

import androidx.annotation.NonNull;

public class Constants {

    public static enum Events {
        EVENT_START("onVideoStart"),
        EVENT_BUFFER("onVideoBuffer"),
        EVENT_ERROR("onVideoError"),
        EVENT_PROGRESS("onVideoProgress"),
        EVENT_PAUSED("onVideoPaused"),
        EVENT_END("onVideoEnd"),
        EVENT_SWITCH("onVideoSwitch");
        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @NonNull
        @Override
        public String toString() {
            return mName;
        }
    }

    public static final String EVENT_PROP_DURATION = "duration";
    public static final String EVENT_PROP_CURRENT_TIME = "currentTime";
}

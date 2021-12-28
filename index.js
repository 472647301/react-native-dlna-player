import PropTypes from "prop-types";
import React, { Component } from "react";
import { StyleSheet, requireNativeComponent } from "react-native";
import { ViewPropTypes, NativeModules, Platform } from "react-native";
import resolveAssetSource from "react-native/Libraries/Image/resolveAssetSource";

const IJKPlayerModule = NativeModules.RNByronVlcModule || {};
const RNByronDLNA = NativeModules.RNByronDLNA || {};

export const startService = (name) => {
  if (RNByronDLNA.startService) {
    RNByronDLNA.startService(name);
  }
};

export const closeService = () => {
  if (RNByronDLNA.closeService) {
    RNByronDLNA.closeService();
  }
};

export const isInstalledApp = (name) => {
  if (RNByronDLNA.isInstalledApp) {
    return RNByronDLNA.isInstalledApp(name).then(res => {
      if (Platform.OS === 'android') {
        return res
      }
      return !!res.installed
    });
  }
};

export const startApp = (name) => {
  if (RNByronDLNA.startApp) {
    RNByronDLNA.startApp(name);
  }
};

export const dlnaEventName = "dlna-player";

export class ByronPlayer extends Component {
  timer = null;
  constructor(props) {
    super(props);
  }

  setNativeProps(nativeProps) {
    this._root?.setNativeProps(nativeProps);
  }

  seek = (time, pauseAfterSeek) => {
    if (!IJKPlayerModule.seek) {
      return;
    }
    IJKPlayerModule.seek(time, pauseAfterSeek);
  };

  setVolume = (volume) => {
    this.setNativeProps({ volume: volume });
  };
  setPaused = (paused) => {
    this.setNativeProps({ paused: paused });
  };

  setMute = (mute) => {
    this.setNativeProps({ mute: mute });
  };

  takeSnapshot = async (path) => {
    if (!IJKPlayerModule.takeSnapshot) {
      return;
    }
    return await IJKPlayerModule.takeSnapshot(path);
  };

  setPan = (pan) => {
    let l = 1;
    let r = 1;
    if (pan < 0) {
      l = 1;
      r = 1 + pan;
    } else {
      r = 1;
      l = 1 - pan;
    }
    if (!IJKPlayerModule.setPan) {
      return;
    }
    IJKPlayerModule.setPan(l, r);
  };

  snapshot = (snapshotPath) => {
    this.setNativeProps({ snapshotPath });
  };

  _assignRoot = (component) => {
    this._root = component;
  };

  setEQPreset = (preset) => {
    if (!IJKPlayerModule.setEQPreset) {
      return;
    }
    IJKPlayerModule.setEQPreset(preset);
  };

  _onLoadStart = (event) => {
    if (Platform.OS === "ios") {
      this.timer = setTimeout(() => {
        if (this.props.onError) {
          this.props.onError();
        }
        this.setNativeProps({ paused: true });
        clearTimeout(this.timer);
        this.timer = null;
      }, this.props.timeout || 30000);
    }
    if (this.props.onLoadStart) {
      this.props.onLoadStart(event.nativeEvent);
    }
  };

  _onLoad = (event) => {
    if (this.timer) {
      clearTimeout(this.timer);
      this.timer = null;
    }
    if (IJKPlayerModule.init) {
      IJKPlayerModule.init();
    }
    if (this.props.onLoad) {
      this.props.onLoad(event.nativeEvent);
    }
  };

  _onError = (event) => {
    if (this.props.onError) {
      this.props.onError(event.nativeEvent);
    }
  };

  _onProgress = (event) => {
    if (this.props.onProgress) {
      this.props.onProgress(event.nativeEvent);
    }
    if (Platform.OS === "ios") {
      const { duration, currentTime } = event.nativeEvent;
      if (duration - currentTime < 250) {
        this.setNativeProps({ paused: true });
        if (this.props.onEnd) {
          this.props.onEnd(event.nativeEvent);
        }
      }
    }
  };

  _onPause = (event) => {
    if (this.props.onPause) {
      this.props.onPause(event.nativeEvent);
    }
  };

  _onStop = (event) => {
    if (this.props.onStop) {
      this.props.onStop(event.nativeEvent);
    }
  };

  _onEnd = (event) => {
    if (this.props.onEnd) {
      this.props.onEnd(event.nativeEvent);
    }
  };

  _onBuffer = (event) => {
    if (this.props.onBuffer) {
      this.props.onBuffer(event.nativeEvent);
    }
  };

  _onTimedText = (event) => {
    if (this.props.onTimedText) {
      this.props.onTimedText(event.nativeEvent);
    }
  };

  setTextTrackIndex = (index) => {
    this.setNativeProps({ selectedTextTrack: index });
  };

  setAudioTrackIndex = (index) => {
    if (IJKPlayerModule.getSelectedTracks) {
      IJKPlayerModule.getSelectedTracks();
    }
    this.setNativeProps({ selectedAudioTrack: index });
  };

  deselectTrack = (index) => {
    this.setNativeProps({ deselectTrack: index });
  };

  _onPlay = () => {
    if (this.props.onPlay) {
      this.props.onPlay();
    }
  };

  render() {
    const source = resolveAssetSource(this.props.source) || {};
    const headers = source.headers ? source.headers : {};
    const userAgent = source.userAgent ? source.userAgent : "";
    const options = source.options ? source.options : [];
    options.push("--input-repeat=1000");
    let uri = source.uri || "";
    if (uri && uri.match(/^\//)) {
      uri = `file://${uri}`;
    }

    const nativeProps = Object.assign({}, this.props);
    Object.assign(nativeProps, {
      style: [styles.base, nativeProps.style],
      src: {
        uri,
        headers,
        userAgent,
        options,
      },
      onVideoLoadStart: this._onLoadStart,
      onVideoLoad: this._onLoad,
      onVideoError: this._onError,
      onVideoProgress: this._onProgress,
      onVideoPause: this._onPause,
      onVideoStop: this._onStop,
      onVideoEnd: this._onEnd,
      onVideoBuffer: this._onBuffer,
      onTimedText: this._onTimedText,
      onPlay: this._onPlay,
    });

    return <RNByronVlc ref={this._assignRoot} {...nativeProps} />;
  }
}

ByronPlayer.propTypes = {
  /* Native only */
  src: PropTypes.object,
  seek: PropTypes.number,
  snapshotPath: PropTypes.string,
  onVideoLoadStart: PropTypes.func,
  onVideoLoad: PropTypes.func,
  onVideoBuffer: PropTypes.func,
  onVideoError: PropTypes.func,
  onVideoProgress: PropTypes.func,
  onVideoPause: PropTypes.func,
  onVideoStop: PropTypes.func,
  onVideoEnd: PropTypes.func,

  /* Wrapper component */
  source: PropTypes.oneOfType([
    PropTypes.shape({
      uri: PropTypes.string,
      headers: PropTypes.object,
      userAgent: PropTypes.string,
    }),
    // Opaque type returned by require('./video.mp4')
    PropTypes.number,
  ]),
  muted: PropTypes.bool,
  volume: PropTypes.number,
  onLoadStart: PropTypes.func,
  onLoad: PropTypes.func,
  onBuffer: PropTypes.func,
  onError: PropTypes.func,
  onProgress: PropTypes.func,
  onPause: PropTypes.func,
  onStop: PropTypes.func,
  onEnd: PropTypes.func,

  /* Required by react-native */
  scaleX: PropTypes.number,
  scaleY: PropTypes.number,
  translateX: PropTypes.number,
  translateY: PropTypes.number,
  rotation: PropTypes.number,
  ...ViewPropTypes,
};

const RNByronVlc = requireNativeComponent("RNByronVlc", ByronPlayer, {
  nativeOnly: {
    src: true,
    seek: true,
    snapshotPath: true,
  },
});

const styles = StyleSheet.create({
  base: {
    overflow: "hidden",
  },
});

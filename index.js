import React, { useRef } from "react";
import { requireNativeComponent, View, Dimensions } from "react-native";
import { NativeModules, NativeEventEmitter } from "react-native";

const { width } = Dimensions.get("window");

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
    return RNByronDLNA.isInstalledApp(name);
  }
};

export const startApp = (name) => {
  if (RNByronDLNA.startApp) {
    RNByronDLNA.startApp(name);
  }
};

export const dlnaEventName = "dlna-player";

const invertKeyValues = (obj) => {
  return Object.keys(obj).reduce((acc, key) => {
    acc[obj[key]] = key;
    return acc;
  }, {});
};

export const ByronEmitter = new NativeEventEmitter(RNByronDLNA);

const RNByronVlc = requireNativeComponent("RNByronVlc");

export const ScaleType = {
  SURFACE_BEST_FIT: 0,
  SURFACE_FIT_SCREEN: 1,
  SURFACE_FILL: 2,
  SURFACE_16_9: 3,
  SURFACE_4_3: 4,
  SURFACE_ORIGINAL: 5,
};

export const EventType = {
  Buffering: 259,
  EncounteredError: 266,
  EndReached: 265,
  Playing: 260,
  ESAdded: 276,
  Paused: 261,
  PositionChanged: 268,
  Stopped: 262,
};

const RNByronPlayer = React.forwardRef((props, ref) => {
  const viewRef = useRef(null);

  React.useImperativeHandle(ref, () => ({
    setNativeProps: (nativeProps) => {
      viewRef.current?.setNativeProps(nativeProps);
    },
  }));

  const onEventVlc = (event) => {
    const data = event.nativeEvent;
    if (
      ![EventType.TimeChanged, EventType.PositionChanged].includes(data.type)
    ) {
      console.log(" >> onEventVlc:", data);
    }
    switch (data.type) {
      case EventType.Buffering:
        props.onBuffering && props.onBuffering();
        break;
      case EventType.EncounteredError:
        props.onError && props.onError();
        break;
      case EventType.EndReached:
        props.onEndReached && props.onEndReached();
        break;
      case EventType.Playing:
        props.onPlaying && props.onPlaying(data);
        break;
      case EventType.Opening:
        props.onOpening && props.onOpening();
        break;
      case EventType.Paused:
        props.onPaused && props.onPaused();
        break;
      case EventType.PositionChanged:
        props.onProgress && props.onProgress(data);
        break;
      case EventType.Stopped:
        props.onStopped && props.onStopped();
        break;
      case EventType.SeekableChanged:
        props.onSeekable && props.onSeekable(data);
        break;
    }
  };

  const onLayout = (e) => {
    const layout = e.nativeEvent.layout;
    viewRef.current?.setNativeProps({
      style: {
        width: layout.width,
        height: layout.height,
      },
    });
  };

  const style = props.style || {};
  const source = props.source || {};
  const options = source.options ? source.options : ["--rtsp-tcp", "-vvv"];
  const uri = source.uri || "";
  if (!uri) return null;

  const nativeProps = Object.assign({}, props, {
    source: { uri, options },
    style: {
      width: style.width || width,
      height: style.height || 240,
    },
  });
  return (
    <View style={style} onLayout={onLayout}>
      <RNByronVlc
        ref={viewRef}
        source={{ uri, options }}
        onEventVlc={onEventVlc}
        {...nativeProps}
      />
    </View>
  );
});

export const ByronPlayer = React.memo(RNByronPlayer);

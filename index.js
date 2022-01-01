import React, { useRef, useEffect, useState } from "react";
import { requireNativeComponent, View } from "react-native";
import { NativeModules, NativeEventEmitter, StyleSheet } from "react-native";

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

export const ByronEmitter = new NativeEventEmitter(RNByronDLNA);

const RNByronVlc = requireNativeComponent("RNByronVlc");

const RNByronPlayer = React.forwardRef((props, ref) => {
  const viewRef = useRef(null);
  const isInit = useRef(false);
  const seek = useRef(0);
  const muted = useRef(false);
  const volume = useRef(1);
  const paused = useRef(true);
  const [width, setWidth] = useState(0);
  const [height, setHeight] = useState(0);

  React.useImperativeHandle(ref, () => ({
    setNativeProps: (nativeProps) => {
      viewRef.current?.setNativeProps(nativeProps);
    },
  }));

  useEffect(() => {
    if (seek.current !== props.seek) {
      seek.current = props.seek;
    }
    if (!isInit.current) {
      return;
    }
    viewRef.current?.setNativeProps({
      seek: seek.current,
    });
  }, [props.seek]);

  useEffect(() => {
    if (muted.current !== props.muted) {
      muted.current = props.muted;
    }
    if (!isInit.current) {
      return;
    }
    viewRef.current?.setNativeProps({
      muted: muted.current,
    });
  }, [props.muted]);

  useEffect(() => {
    if (volume.current !== props.volume) {
      volume.current = props.volume;
    }
    if (!isInit.current) {
      return;
    }
    viewRef.current?.setNativeProps({
      volume: volume.current,
    });
  }, [props.volume]);

  useEffect(() => {
    if (paused.current !== props.paused) {
      paused.current = props.paused;
    }
    if (!isInit.current) {
      return;
    }
    viewRef.current?.setNativeProps({
      paused: paused.current,
    });
  }, [props.paused]);

  const onVideoStart = (event) => {
    isInit.current = true;
    viewRef.current?.setNativeProps({
      paused: paused.current,
    });
    if (seek.current) {
      viewRef.current?.setNativeProps({
        seek: seek.current,
      });
    }
    if (volume.current) {
      viewRef.current?.setNativeProps({
        volume: volume.current,
      });
    }
    if (muted.current) {
      viewRef.current?.setNativeProps({
        muted: muted.current,
      });
    }
    if (props.onStart) {
      props.onStart(event.nativeEvent);
    }
  };
  const onVideoBuffer = () => {
    if (props.onBuffer) {
      props.onBuffer();
    }
  };
  const onVideoError = () => {
    if (props.onError) {
      props.onError();
    }
  };
  const onVideoProgress = (event) => {
    const info = event.nativeEvent;
    const duration = info.duration || 0;
    const currentTime = info.currentTime || 0;
    if (seek.current && duration) {
      const nowSeek = Math.ceil(duration * seek.current);
      if (currentTime < nowSeek) {
        return;
      }
    }
    if (props.onProgress) {
      props.onProgress(info);
    }
  };
  const onVideoPaused = (event) => {
    const info = event.nativeEvent || {};
    if (paused.current !== info.paused) {
      return;
    }
    if (props.onPaused) {
      props.onPaused(info.paused);
    }
  };
  const onVideoEnd = () => {
    if (props.onEnd) {
      props.onEnd();
    }
  };
  const onVideoSwitch = () => {
    isInit.current = false;
    if (props.onSwitch) {
      props.onSwitch();
    }
  };

  const onLayout = (e) => {
    const layout = e.nativeEvent.layout;
    if (layout.width !== width) {
      setWidth(layout.width);
    }
    if (layout.height !== height) {
      setHeight(layout.height);
    }
  };

  const source = props.source || {};
  const headers = source.headers ? source.headers : {};
  const userAgent = source.userAgent ? source.userAgent : "";
  const uri = source.uri || "";
  if (!uri) return null;

  return (
    <View style={props.style} onLayout={onLayout}>
      {width && height ? (
        <RNByronVlc
          ref={viewRef}
          width={width}
          height={height}
          src={{ uri, headers, userAgent }}
          style={[styles.video, { width, height }]}
          onVideoStart={onVideoStart}
          onVideoBuffer={onVideoBuffer}
          onVideoError={onVideoError}
          onVideoProgress={onVideoProgress}
          onVideoPaused={onVideoPaused}
          onVideoEnd={onVideoEnd}
          onVideoSwitch={onVideoSwitch}
        />
      ) : null}
    </View>
  );
});

export const ByronPlayer = React.memo(RNByronPlayer);

const styles = StyleSheet.create({
  video: {
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#000",
  },
});

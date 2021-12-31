import React, { useRef, useEffect } from "react";
import { requireNativeComponent, Dimensions } from "react-native";
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

export const ByronEmitter = new NativeEventEmitter(RNByronDLNA);

const RNByronVlc = requireNativeComponent("RNByronVlc");

const RNByronPlayer = (props) => {
  const viewRef = useRef(null);
  const isInit = useRef(false);
  const seek = useRef(0);
  const muted = useRef(false);
  const volume = useRef(1);
  const paused = useRef(true);
  const size = useRef({
    width: props.style?.width || width,
    height: props.style?.height || 240,
  });

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

  useEffect(() => {
    const style = props.style || {};
    if (!style.width && !style.height) {
      return;
    }
    if (size.current.width !== style.width) {
      size.current.width = style.width;
    }
    if (size.current.height !== style.height) {
      size.current.height = style.height;
    }
    viewRef.current?.setNativeProps({
      width: size.current.width,
      height: size.current.height,
    });
  }, [props.style]);

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
    if (props.onPaused) {
      props.onPaused(event.nativeEvent?.paused);
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

  const source = props.source || {};
  const headers = source.headers ? source.headers : {};
  const userAgent = source.userAgent ? source.userAgent : "";
  const uri = source.uri || "";
  if (!uri) return null;

  return (
    <RNByronVlc
      ref={viewRef}
      src={{ uri, headers, userAgent }}
      width={size.current.width}
      height={size.current.height}
      onVideoStart={onVideoStart}
      onVideoBuffer={onVideoBuffer}
      onVideoError={onVideoError}
      onVideoProgress={onVideoProgress}
      onVideoPaused={onVideoPaused}
      onVideoEnd={onVideoEnd}
      onVideoSwitch={onVideoSwitch}
    />
  );
};

export const ByronPlayer = React.memo((props) => {
  return React.createElement(RNByronPlayer, props);
});

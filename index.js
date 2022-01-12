import React from "react";
import { requireNativeComponent } from "react-native";
import { NativeModules, NativeEventEmitter } from "react-native";

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

export const ScaleType = {
  SURFACE_BEST_FIT: 0,
  SURFACE_FIT_SCREEN: 1,
  SURFACE_FILL: 2,
  SURFACE_16_9: 3,
  SURFACE_4_3: 4,
  SURFACE_ORIGINAL: 5,
};

export class ByronPlayer extends React.Component {
  render() {
    return <RNByronVlc {...this.props} />;
  }
}

declare module "@byron-react-native/dlna-player" {
  import { ViewProps, NativeEventEmitter } from "react-native";

  export interface VlcEvent {
    /**
     * 已播时长（毫秒ms单位）
     */
    currentTime: number;
    /**
     * 总时长（毫秒ms单位）
     */
    duration: number;
    /**
     * 进度
     */
    position: number;
  }
  export interface VlcSource {
    uri: string;
    options: Array<string>;
  }
  export enum ScaleType {
    SURFACE_BEST_FIT,
    SURFACE_FIT_SCREEN,
    SURFACE_FILL,
    SURFACE_16_9,
    SURFACE_4_3,
    SURFACE_ORIGINAL,
  }
  export interface VlcProps extends ViewProps {
    source: VlcSource;
    time: number;
    position: number;
    rate: ScaleType;
    paused: boolean;
    aspectRatio: string;
    volume: number;
    onStart: (event: VlcEvent) => void;
    onBuffer: () => void;
    onLoading: () => void;
    onError: () => void;
    onProgress: (event: VlcEvent) => void;
    onEnd: () => void;
    onSwitch: () => void;
    onPaused: (paused: boolean) => void;
  }
  export class ByronPlayer extends React.Component<Partial<VlcProps>> {}

  export function startService(serverName: string): void;
  export function closeService(): void;
  export function isInstalledApp(bid: string): Promise<boolean>;
  export function startApp(bid: string): void;
  export const dlnaEventName = "dlna-player";
  export const ByronEmitter: NativeEventEmitter;
}

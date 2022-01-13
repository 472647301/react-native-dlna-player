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
    type: EventType;
  }
  export interface VlcSource {
    uri: string;
    options?: Array<string>;
  }
  export enum ScaleType {
    SURFACE_BEST_FIT,
    SURFACE_FIT_SCREEN,
    SURFACE_FILL,
    SURFACE_16_9,
    SURFACE_4_3,
    SURFACE_ORIGINAL,
  }
  export enum EventType {
    Buffering = 259,
    EncounteredError = 266,
    EndReached = 265,
    Playing = 260,
    MediaChanged = 256,
    ESAdded = 276,
    ESDeleted = 277,
    ESSelected = 278,
    LengthChanged = 273,
    Opening = 258,
    PausableChanged = 270,
    Paused = 261,
    PositionChanged = 268,
    Stopped = 262,
    RecordChanged = 286,
    SeekableChanged = 269,
    Vout = 274,
  }
  export interface VlcProps extends ViewProps {
    source: VlcSource;
    time: number;
    rate: ScaleType;
    paused: boolean;
    aspectRatio: string;
    volume: number;
    onBuffering: () => void;
    onError: () => void;
    onEndReached: () => void;
    onPlaying: (event: VlcEvent) => void;
    onOpening: () => void;
    onPaused: () => void;
    onProgress: (event: VlcEvent) => void;
    onStopped: () => void;
    onEventVlc: (event: VlcEvent) => void;
  }
  type INativeProps =
    | { time: number }
    | { rate: ScaleType }
    | { paused: boolean }
    | { aspectRatio: string }
    | { volume: number };
  export class ByronPlayer extends React.Component<Partial<VlcProps>> {
    setNativeProps(nativeProps: INativeProps): void;
  }

  export function startService(serverName: string): void;
  export function closeService(): void;
  export function isInstalledApp(bid: string): Promise<boolean>;
  export function startApp(bid: string): void;
  export const dlnaEventName = "dlna-player";
  export const ByronEmitter: NativeEventEmitter;
}

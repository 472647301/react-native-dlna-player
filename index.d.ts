declare module "@byron-react-native/dlna-player" {
  import { ViewProps, NativeEventEmitter } from "react-native";

  export interface ByronPlayerSource {
    uri: string;
    headers: { [key: string]: string };
    userAgent: string;
  }

  export interface ByronPlayerEvent {
    /**
     * 已播时长（毫秒ms单位）
     */
    currentTime: number;
    /**
     * 总时长（毫秒ms单位）
     */
    duration: number;
  }
  export interface ByronPlayerProps extends ViewProps {
    src: ByronPlayerSource;
    seek: number;
    source: Partial<ByronPlayerSource> | number;
    muted: boolean;
    volume: number;
    paused: boolean;
    onStart: (event: ByronPlayerEvent) => void;
    onBuffer: () => void;
    onLoading: () => void;
    onError: () => void;
    onProgress: (event: ByronPlayerEvent) => void;
    onEnd: () => void;
    onSwitch: () => void;
    onPaused: (paused: boolean) => void;
  }
  export class ByronPlayer extends React.Component<Partial<ByronPlayerProps>> {
    setNativeProps(nativeProps: any): void;
  }

  export function startService(serverName: string): void;
  export function closeService(): void;
  export function isInstalledApp(bid: string): Promise<boolean>;
  export function startApp(bid: string): void;
  export const dlnaEventName = "dlna-player";
  export const ByronEmitter: NativeEventEmitter;
}

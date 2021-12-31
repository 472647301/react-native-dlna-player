declare module "@byron-react-native/dlna-player" {
  import { ViewProps, NativeMethods, NativeEventEmitter } from "react-native";

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
  type Constructor<T> = new (...args: any[]) => T;
  class ViewComponent extends React.Component<ByronPlayerProps> {}
  const ViewBase: Constructor<NativeMethods> & typeof ViewComponent;
  export class ByronPlayer extends ViewBase {
    /**
     * Is 3D Touch / Force Touch available (i.e. will touch events include `force`)
     * @platform ios
     */
    static forceTouchAvailable: boolean;
  }

  export function startService(serverName: string): void;
  export function closeService(): void;
  export function isInstalledApp(bid: string): Promise<boolean>;
  export function startApp(bid: string): void;
  export const dlnaEventName = "dlna-player";
  export const ByronEmitter: NativeEventEmitter;
}

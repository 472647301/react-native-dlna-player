declare module "@byron-react-native/dlna-player" {
  import { ViewProps } from "react-native";

  export interface ByronPlayerSource {
    uri: string;
    headers: { [key: string]: string };
    userAgent: string;
    /**
     * VLCMedia options
     */
    options: Array<string>;
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
    snapshotPath: string;
    timeout: number;
    source: Partial<ByronPlayerSource> | number;
    muted: boolean;
    volume: number;
    paused: boolean;
    onLoad: (event: ByronPlayerEvent) => void;
    onBuffer: () => void;
    onError: () => void;
    onProgress: (event: ByronPlayerEvent) => void;
    onEnd: () => void;
    onPause: (event: { paused: boolean }) => void;
    scaleX: number;
    scaleY: number;
    translateX: number;
    translateY: number;
    rotation: number;
  }
  export class ByronPlayer extends React.Component<Partial<ByronPlayerProps>> {
    setNativeProps: ({}: { [key: string]: boolean | number | string }) => void;
  }

  export function startService(serverName: string): void;
  export function closeService(): void;
  export function isInstalledApp(): Promise<boolean>;
  export function startApp(): void;
  export const dlnaEventName = "dlna-player";
}

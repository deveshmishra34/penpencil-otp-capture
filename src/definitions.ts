declare module '@capacitor/core' {
  interface PluginRegistry {
    OtpCapturePlugin: OtpCapturePluginPlugin;
  }
}

export interface OtpCapturePluginPlugin {
  startSmsUserConsent(): Promise<void>;
}

export interface OtpCapturePluginPlugin {
  stopSmsUserConsent(): Promise<void>;
}

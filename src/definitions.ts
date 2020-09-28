declare module '@capacitor/core' {
  interface PluginRegistry {
    OtpCapturePlugin: OtpCapturePluginPlugin;
  }
}

export interface OtpCapturePluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}

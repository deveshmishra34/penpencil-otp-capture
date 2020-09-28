import { WebPlugin } from '@capacitor/core';
import { OtpCapturePluginPlugin } from './definitions';

export class OtpCapturePluginWeb extends WebPlugin implements OtpCapturePluginPlugin {
  constructor() {
    super({
      name: 'OtpCapturePlugin',
      platforms: ['web'],
    });
  }

  async startSmsUserConsent(): Promise<void> {
    return undefined;
  }

  async stopSmsUserConsent(): Promise<void> {
    return undefined;
  }
}

const OtpCapturePlugin = new OtpCapturePluginWeb();

export { OtpCapturePlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(OtpCapturePlugin);

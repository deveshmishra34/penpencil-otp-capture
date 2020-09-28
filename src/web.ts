import { WebPlugin } from '@capacitor/core';
import { OtpCapturePluginPlugin } from './definitions';

export class OtpCapturePluginWeb extends WebPlugin implements OtpCapturePluginPlugin {
  constructor() {
    super({
      name: 'OtpCapturePlugin',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const OtpCapturePlugin = new OtpCapturePluginWeb();

export { OtpCapturePlugin };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(OtpCapturePlugin);

import { config } from './config';

// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

/**
 * Environment variables for development mode.
 */
export const environment: any = {
  ...config,
  production: false,
  backendUrl: 'http://localhost:8080',
  frontendUrl: 'http://localhost:4200',
  withCredentials: true,
  firebaseConfig: {
    apiKey: 'AIzaSyBjvTGcPWgDf_7JxwdyXgjClkBSw9YPMNo',
    authDomain: 'teammates-john-354208.firebaseapp.com',
    projectId: 'teammates-john-354208',
    storageBucket: 'teammates-john-354208.appspot.com',
    messagingSenderId: '693245700352',
    appId: '1:693245700352:web:f3d2e5dd5568c7cf0debe5',
    measurementId: 'G-32FP06NV3X',
  },
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.

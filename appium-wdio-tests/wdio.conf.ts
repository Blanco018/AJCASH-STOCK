import type { Options } from '@wdio/types';
import path from 'path';

export const config: Options.Testrunner = {
    // Runner y ejecución local
    runner: 'local',
    autoCompileOpts: {
        autoCompile: true,
        tsNodeOpts: {
            project: path.join(__dirname, 'tsconfig.json'),
            transpileOnly: true
        }
    },

    // Definición de specs / pruebas a ejecutar
    specs: [
        './src/specs/**/*.spec.ts'
    ],
    exclude: [],

    maxInstances: 1,

    // Configuración del servidor Appium
    hostname: '127.0.0.1',
    port: 4723,
    path: '/',

    // Capacidades de Android para UiAutomator2
    capabilities: [{
        platformName: 'Android',
        'appium:automationName': 'UiAutomator2',
        'appium:deviceName': 'Android_Device',
        // Ruta al binario APK compilado por Gradle
        'appium:app': path.resolve(__dirname, '../app/build/outputs/apk/debug/app-debug.apk'),
        'appium:appPackage': 'com.aistudio.ajcashstock.vkpq',
        'appium:appActivity': 'com.example.MainActivity',
        'appium:appWaitActivity': 'com.example.MainActivity,com.example.*',
        'appium:noReset': false,
        'appium:fullReset': false,
        'appium:autoGrantPermissions': true,
        'appium:newCommandTimeout': 180,
        'appium:uiautomator2ServerLaunchTimeout': 60000,
        'appium:ensureWebviewsHavePages': true
    }],

    // Nivel de log y tiempos de espera
    logLevel: 'info',
    bail: 0,
    waitforTimeout: 15000,
    connectionRetryTimeout: 120000,
    connectionRetryCount: 3,

    // Si levantas Appium en una terminal separada (recomendado en Windows),
    // dejamos services vacío para conectarnos directamente al puerto 4723.
    services: [],

    framework: 'mocha',
    reporters: ['spec'],

    mochaOpts: {
        ui: 'bdd',
        timeout: 90000
    }
};

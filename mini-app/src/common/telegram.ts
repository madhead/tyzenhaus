import {
    init,
    isTMA,
    miniApp,
    retrieveLaunchParams,
    retrieveRawInitData,
    themeParams,
    viewport,
} from "@tma.js/sdk-react";

/**
 * Whether the app is opened inside Telegram. The launch parameters are available only there.
 */
export function isInsideTelegram(): boolean {
    return isTMA();
}

/**
 * Initializes the SDK, exposes the Telegram theme as `--tg-theme-*` CSS variables and expands the app to the full
 * height available. No-op outside Telegram.
 */
export function initTelegram(): void {
    if (!isInsideTelegram()) {
        return;
    }

    init();
    themeParams.mount();
    themeParams.bindCssVars();
    miniApp.mount();

    viewport
        .mount()
        .then(() => {
            viewport.expand.ifAvailable();
        })
        .catch((error: unknown) => {
            console.error("Failed to expand the app", error);
        });
}

/**
 * Tells Telegram that the app is ready to be shown, which dismisses its loading placeholder. No-op outside Telegram.
 */
export function notifyReady(): void {
    if (isInsideTelegram()) {
        miniApp.ready.ifAvailable();
    }
}

/**
 * The launch `start_param`, which carries the bearer token issued by the bot.
 */
export function startParam(): string | undefined {
    return isInsideTelegram() ? retrieveLaunchParams().tgWebAppStartParam : undefined;
}

/**
 * Raw `initData` for the backend to validate.
 */
export function rawInitData(): string | undefined {
    return isInsideTelegram() ? retrieveRawInitData() : undefined;
}

/**
 * The Telegram user's interface language.
 */
export function languageCode(): string | undefined {
    return isInsideTelegram() ? retrieveLaunchParams().tgWebAppData?.user?.language_code : undefined;
}

import { init, isTMA, retrieveLaunchParams, retrieveRawInitData, themeParams } from "@tma.js/sdk-react";

/**
 * Whether the app is opened inside Telegram. The launch parameters are available only there.
 */
export function isInsideTelegram(): boolean {
    return isTMA();
}

/**
 * Initializes the SDK and exposes the Telegram theme as `--tg-theme-*` CSS variables. No-op outside Telegram.
 */
export function initTelegram(): void {
    if (!isInsideTelegram()) {
        return;
    }

    init();
    themeParams.mount();
    themeParams.bindCssVars();
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

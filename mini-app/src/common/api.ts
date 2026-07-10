import WebApp from "@twa-dev/sdk";

/**
 * Headers sent with every Mini App API request:
 * - the bearer token (group binding and scope) issued by the bot and passed as the launch `start_param`.
 * - raw `initData` the backend validates to authenticate the Telegram user.
 */
export function authHeaders(): Record<string, string> {
    return {
        "Authorization": `Bearer ${WebApp.initDataUnsafe.start_param}`,
        "X-Telegram-Init-Data": WebApp.initData,
    };
}

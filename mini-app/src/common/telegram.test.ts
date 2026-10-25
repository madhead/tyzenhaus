import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { initTelegram, notifyReady } from "./telegram";

const sdk = vi.hoisted(() => ({
    isTMA: vi.fn(),
    init: vi.fn(),
    themeParams: { mount: vi.fn(), bindCssVars: vi.fn() },
    miniApp: { mount: vi.fn(), ready: { ifAvailable: vi.fn() } },
    viewport: { mount: vi.fn(), expand: { ifAvailable: vi.fn() } },
    retrieveLaunchParams: vi.fn(),
    retrieveRawInitData: vi.fn(),
}));

vi.mock("@tma.js/sdk-react", () => sdk);

describe("telegram", () => {
    beforeEach(() => {
        sdk.isTMA.mockReturnValue(true);
        sdk.viewport.mount.mockResolvedValue(undefined);
    });

    afterEach(() => {
        vi.restoreAllMocks();
        vi.clearAllMocks();
    });

    describe("initTelegram", () => {
        it("does nothing outside of Telegram", () => {
            sdk.isTMA.mockReturnValue(false);

            initTelegram();

            expect(sdk.init).not.toHaveBeenCalled();
            expect(sdk.viewport.mount).not.toHaveBeenCalled();
        });

        it("binds the theme to CSS variables", () => {
            initTelegram();

            expect(sdk.init).toHaveBeenCalledOnce();
            expect(sdk.themeParams.mount).toHaveBeenCalledOnce();
            expect(sdk.themeParams.bindCssVars).toHaveBeenCalledOnce();
        });

        it("expands the app once the viewport is mounted", async () => {
            initTelegram();

            expect(sdk.viewport.expand.ifAvailable).not.toHaveBeenCalled();
            await vi.waitFor(() => expect(sdk.viewport.expand.ifAvailable).toHaveBeenCalledOnce());
        });

        it("survives a viewport that cannot be mounted", async () => {
            const error = vi.spyOn(console, "error").mockImplementation(() => undefined);
            sdk.viewport.mount.mockRejectedValue(new Error("unsupported"));

            initTelegram();

            await vi.waitFor(() => expect(error).toHaveBeenCalled());
            expect(sdk.viewport.expand.ifAvailable).not.toHaveBeenCalled();
        });
    });

    describe("notifyReady", () => {
        it("tells Telegram the app is ready", () => {
            notifyReady();

            expect(sdk.miniApp.ready.ifAvailable).toHaveBeenCalledOnce();
        });

        it("does nothing outside of Telegram", () => {
            sdk.isTMA.mockReturnValue(false);

            notifyReady();

            expect(sdk.miniApp.ready.ifAvailable).not.toHaveBeenCalled();
        });
    });
});

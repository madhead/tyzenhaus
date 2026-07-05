import { render, screen, waitFor } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import AppWrapper from "./AppWrapper";

const webApp = vi.hoisted(() => ({
    platform: "ios",
    initData: "init-data-string",
    initDataUnsafe: { start_param: "token-123" },
}));

vi.mock("@twa-dev/sdk", () => ({
    default: webApp,
}));

vi.mock("react-i18next", () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

function mockFetch(response: { ok: boolean; headers?: Record<string, string> }) {
    const fetchMock = vi.fn().mockResolvedValue({
        ok: response.ok,
        headers: new Headers(response.headers ?? {}),
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

describe("AppWrapper", () => {
    beforeEach(() => {
        webApp.platform = "ios";
        webApp.initData = "init-data-string";
        webApp.initDataUnsafe = { start_param: "token-123" };
    });

    afterEach(() => {
        vi.unstubAllGlobals();
        vi.clearAllMocks();
    });

    it("shows an error and skips authentication when opened outside of Telegram", () => {
        webApp.platform = "unknown";
        const fetchMock = mockFetch({ ok: true });

        render(
            <AppWrapper>
                <div>protected content</div>
            </AppWrapper>,
        );

        expect(screen.getByText("errors.outsideOfTelegram")).toBeInTheDocument();
        expect(screen.queryByText("protected content")).not.toBeInTheDocument();
        expect(fetchMock).not.toHaveBeenCalled();
    });

    it("renders children once authentication succeeds", async () => {
        mockFetch({ ok: true });

        render(
            <AppWrapper>
                <div>protected content</div>
            </AppWrapper>,
        );

        expect(await screen.findByText("protected content")).toBeInTheDocument();
    });

    it("sends the start_param as a bearer token and the init data as the body", async () => {
        const fetchMock = mockFetch({ ok: true });

        render(
            <AppWrapper>
                <div>protected content</div>
            </AppWrapper>,
        );

        await screen.findByText("protected content");

        expect(fetchMock).toHaveBeenCalledWith("/app/api/auth/validation", {
            method: "POST",
            headers: { Authorization: "Bearer token-123" },
            body: "init-data-string",
        });
    });

    it("shows the expired error when the token has expired", async () => {
        mockFetch({ ok: false, headers: { "X-Token-Expired": "true" } });

        render(
            <AppWrapper>
                <div>protected content</div>
            </AppWrapper>,
        );

        expect(await screen.findByText("errors.expired")).toBeInTheDocument();
        expect(screen.queryByText("protected content")).not.toBeInTheDocument();
    });

    it("shows the unauthorized error when authentication fails without an expiry hint", async () => {
        mockFetch({ ok: false });

        render(
            <AppWrapper>
                <div>protected content</div>
            </AppWrapper>,
        );

        expect(await screen.findByText("errors.unauthorized")).toBeInTheDocument();
        expect(screen.queryByText("protected content")).not.toBeInTheDocument();
    });

    it("renders nothing while authentication is in flight", async () => {
        let resolve!: (value: { ok: boolean; headers: Headers }) => void;
        const fetchMock = vi.fn().mockReturnValue(
            new Promise((r) => {
                resolve = r;
            }),
        );
        vi.stubGlobal("fetch", fetchMock);

        const { container } = render(
            <AppWrapper>
                <div>protected content</div>
            </AppWrapper>,
        );

        expect(container).toBeEmptyDOMElement();
        expect(screen.queryByText("protected content")).not.toBeInTheDocument();

        resolve({ ok: true, headers: new Headers() });
        await waitFor(() => expect(screen.getByText("protected content")).toBeInTheDocument());
    });
});

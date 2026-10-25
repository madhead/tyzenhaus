import { act, render, screen, waitFor } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { GroupMember } from "../common/api";
import { membersCache } from "../common/members";
import { Transaction } from "../common/transaction/Transaction";
import HistoryApp from "./HistoryApp";

vi.mock("react-i18next", () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

vi.mock("../common/telegram", () => ({
    startParam: () => "token-123",
    rawInitData: () => "init-data-string",
}));

const expectedHeaders = {
    "Authorization": "Bearer token-123",
    "X-Telegram-Init-Data": "init-data-string",
};

function makeTransaction(overrides: Partial<Transaction> = {}): Transaction {
    return {
        id: 1,
        payer: 42,
        recipients: [1, 2],
        amount: "10.00",
        currency: "EUR",
        title: "Groceries",
        timestamp: Date.UTC(2024, 0, 5, 12, 0, 0),
        ...overrides,
    };
}

type Page = { transactions: Transaction[]; nextCursor: string | null };

/**
 * Serves the members and the pages of transactions, keyed by the requested cursor (`""` for the first page).
 */
function mockFetch(
    pages: Record<string, Page> | Transaction[],
    members: GroupMember[] = [],
    server: { failOn: (url: string) => boolean } = { failOn: () => false },
) {
    const byCursor = Array.isArray(pages) ? { "": { transactions: pages, nextCursor: null } } : pages;
    const fetchMock = vi.fn().mockImplementation((url: string) => {
        if (server.failOn(url)) {
            return Promise.resolve({ ok: false, status: 500, json: () => Promise.resolve({}) });
        }

        const cursor = new URL(url, "http://localhost").searchParams.get("cursor") ?? "";
        const body = url === "/app/api/group/members" ? { id: 1, members } : byCursor[cursor];

        return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(body) });
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

class FakeIntersectionObserver {
    static observers: FakeIntersectionObserver[] = [];

    observed = false;

    constructor(private readonly callback: IntersectionObserverCallback) {
        FakeIntersectionObserver.observers.push(this);
    }

    observe() {
        this.observed = true;
    }

    disconnect() {
        this.observed = false;
    }

    /**
     * Reports the sentinel as scrolled into view to every observer that's still active.
     */
    static scrollToEnd() {
        for (const observer of FakeIntersectionObserver.observers.filter((o) => o.observed)) {
            observer.callback(
                [{ isIntersecting: true } as IntersectionObserverEntry],
                observer as unknown as IntersectionObserver,
            );
        }
    }
}

describe("HistoryApp", () => {
    beforeEach(() => {
        FakeIntersectionObserver.observers = [];
        vi.stubGlobal("IntersectionObserver", FakeIntersectionObserver);
    });

    afterEach(() => {
        vi.unstubAllGlobals();
        vi.restoreAllMocks();
        membersCache.reset();
    });

    it("requests the transactions and the members with the bearer token", async () => {
        const fetchMock = mockFetch([]);

        render(<HistoryApp />);

        await waitFor(() =>
            expect(fetchMock).toHaveBeenCalledWith("/app/api/group/transactions", {
                method: "GET",
                headers: expectedHeaders,
            }),
        );
        expect(fetchMock).toHaveBeenCalledWith("/app/api/group/members", {
            method: "GET",
            headers: expectedHeaders,
        });
    });

    it("renders a card for each returned transaction", async () => {
        mockFetch([
            makeTransaction({ id: 1, title: "Groceries" }),
            makeTransaction({ id: 2, title: "Rent" }),
            makeTransaction({ id: 3, title: "Coffee" }),
        ]);

        const { container } = render(<HistoryApp />);

        expect(await screen.findByText("Groceries")).toBeInTheDocument();
        expect(screen.getByText("Rent")).toBeInTheDocument();
        expect(screen.getByText("Coffee")).toBeInTheDocument();
        expect(container.querySelectorAll(".transaction")).toHaveLength(3);
    });

    it("renders the transactions as a semantic list", async () => {
        mockFetch([makeTransaction({ id: 1, title: "Rent" }), makeTransaction({ id: 2, title: "Coffee" })]);

        const { container } = render(<HistoryApp />);

        await screen.findByText("Rent");
        expect(container.querySelector("ul.history")).toBeInTheDocument();
        expect(container.querySelectorAll("ul.history > li")).toHaveLength(2);
    });

    it("renders resolved member names in the cards", async () => {
        mockFetch(
            [makeTransaction({ payer: 42, recipients: [1] })],
            [
                { id: 42, firstName: "Ada", lastName: "Lovelace", username: null },
                { id: 1, firstName: "Grace", lastName: "Hopper", username: null },
            ],
        );

        render(<HistoryApp />);

        expect(await screen.findByText("Ada Lovelace")).toBeInTheDocument();
        expect(screen.getByText("Grace Hopper")).toBeInTheDocument();
    });

    it("renders no cards when the history is empty", async () => {
        const fetchMock = mockFetch([]);

        const { container } = render(<HistoryApp />);

        await waitFor(() => expect(fetchMock).toHaveBeenCalled());
        expect(container.querySelectorAll(".transaction")).toHaveLength(0);
    });

    it("loads the next page by cursor when the end of the list is reached", async () => {
        const fetchMock = mockFetch({
            "": { transactions: [makeTransaction({ id: 2, title: "Rent" })], nextCursor: "c1" },
            "c1": { transactions: [makeTransaction({ id: 1, title: "Coffee" })], nextCursor: null },
        });

        const { container } = render(<HistoryApp />);

        await screen.findByText("Rent");
        expect(screen.queryByText("Coffee")).not.toBeInTheDocument();

        act(() => {
            FakeIntersectionObserver.scrollToEnd();
        });

        expect(await screen.findByText("Coffee")).toBeInTheDocument();
        expect(fetchMock).toHaveBeenCalledWith("/app/api/group/transactions?cursor=c1", {
            method: "GET",
            headers: expectedHeaders,
        });
        expect(container.querySelectorAll(".transaction")).toHaveLength(2);
    });

    it("stops paging once the last page is loaded", async () => {
        const fetchMock = mockFetch({
            "": { transactions: [makeTransaction({ id: 2, title: "Rent" })], nextCursor: "c1" },
            "c1": { transactions: [makeTransaction({ id: 1, title: "Coffee" })], nextCursor: null },
        });

        const { container } = render(<HistoryApp />);

        await screen.findByText("Rent");
        act(() => {
            FakeIntersectionObserver.scrollToEnd();
        });
        await screen.findByText("Coffee");

        expect(container.querySelector(".sentinel")).not.toBeInTheDocument();
        act(() => {
            FakeIntersectionObserver.scrollToEnd();
        });
        expect(fetchMock.mock.calls.filter(([url]) => String(url).includes("cursor"))).toHaveLength(1);
    });

    it("has no end-of-list marker when everything fits on one page", async () => {
        mockFetch([makeTransaction({ title: "Rent" })]);

        const { container } = render(<HistoryApp />);

        await screen.findByText("Rent");
        expect(container.querySelector(".sentinel")).not.toBeInTheDocument();
    });

    it("shows a loading indicator until the first page arrives", async () => {
        mockFetch([makeTransaction({ title: "Rent" })]);

        render(<HistoryApp />);

        expect(screen.getByRole("status")).toHaveTextContent("loading");
        await screen.findByText("Rent");
        expect(screen.queryByRole("status")).not.toBeInTheDocument();
    });

    it("says so when there are no transactions", async () => {
        mockFetch([]);

        render(<HistoryApp />);

        expect(await screen.findByText("history.empty")).toBeInTheDocument();
    });

    it("shows an error with a retry button when the first page fails, and recovers on retry", async () => {
        vi.spyOn(console, "error").mockImplementation(() => undefined);
        const server: { failOn: (url: string) => boolean } = { failOn: (url) => url === "/app/api/group/transactions" };
        mockFetch([makeTransaction({ title: "Rent" })], [], server);

        render(<HistoryApp />);

        expect(await screen.findByRole("alert")).toHaveTextContent("errors.loadFailed");

        server.failOn = () => false;
        screen.getByRole("button", { name: "retry" }).click();

        expect(await screen.findByText("Rent")).toBeInTheDocument();
        expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    });

    it("keeps the loaded transactions when a following page fails, and retries that page", async () => {
        vi.spyOn(console, "error").mockImplementation(() => undefined);
        const server: { failOn: (url: string) => boolean } = { failOn: (url) => url.includes("cursor=c1") };
        mockFetch(
            {
                "": { transactions: [makeTransaction({ id: 2, title: "Rent" })], nextCursor: "c1" },
                "c1": { transactions: [makeTransaction({ id: 1, title: "Coffee" })], nextCursor: null },
            },
            [],
            server,
        );

        render(<HistoryApp />);
        await screen.findByText("Rent");

        act(() => {
            FakeIntersectionObserver.scrollToEnd();
        });

        expect(await screen.findByRole("alert")).toHaveTextContent("errors.loadFailed");
        expect(screen.getByText("Rent")).toBeInTheDocument();

        server.failOn = () => false;
        screen.getByRole("button", { name: "retry" }).click();

        expect(await screen.findByText("Coffee")).toBeInTheDocument();
        expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    });

    it("falls back to member IDs when the members cannot be loaded", async () => {
        vi.spyOn(console, "error").mockImplementation(() => undefined);
        mockFetch([makeTransaction({ payer: 42, recipients: [1] })], [], {
            failOn: (url) => url === "/app/api/group/members",
        });

        render(<HistoryApp />);

        expect(await screen.findByText("#42")).toBeInTheDocument();
        expect(screen.getByText("#1")).toBeInTheDocument();
    });
});

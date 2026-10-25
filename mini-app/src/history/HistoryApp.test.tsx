import { act, fireEvent, render, screen, waitFor, within } from "@testing-library/react";
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

type Server = { failOn: (url: string) => boolean; currencies: string[] };

/**
 * Serves the members, the currencies and the transactions: either pages keyed by the requested cursor (`""` for the
 * first page), or whatever [pages] makes of the transactions request.
 */
function mockFetch(
    pages: Record<string, Page> | Transaction[] | ((query: URLSearchParams) => Transaction[]),
    members: GroupMember[] = [],
    server: Server = { failOn: () => false, currencies: [] },
) {
    const fetchMock = vi.fn().mockImplementation((url: string) => {
        if (server.failOn(url)) {
            return Promise.resolve({ ok: false, status: 500, json: () => Promise.resolve({}) });
        }

        const query = new URL(url, "http://localhost").searchParams;
        let body: unknown;

        if (url === "/app/api/group/members") {
            body = { id: 1, members };
        } else if (url === "/app/api/group/currencies") {
            body = server.currencies;
        } else if (typeof pages === "function") {
            body = { transactions: pages(query), nextCursor: null };
        } else if (Array.isArray(pages)) {
            body = { transactions: pages, nextCursor: null };
        } else {
            body = pages[query.get("cursor") ?? ""];
        }

        return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(body) });
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

/**
 * The query of the latest transactions search.
 */
function lastSearch(fetchMock: ReturnType<typeof mockFetch>): URLSearchParams {
    const urls = fetchMock.mock.calls
        .map(([url]) => String(url))
        .filter((url) => url.startsWith("/app/api/group/transactions"));

    return new URL(urls[urls.length - 1], "http://localhost").searchParams;
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

        const { container } = render(<HistoryApp />);

        await waitFor(() => expect(container.querySelector("ul.history")).toBeInTheDocument());
        const list = container.querySelector<HTMLElement>("ul.history")!;

        expect(await within(list).findByText("Ada Lovelace")).toBeInTheDocument();
        expect(within(list).getByText("Grace Hopper")).toBeInTheDocument();
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
        const server: Server = { failOn: (url) => url === "/app/api/group/transactions", currencies: [] };
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
        const server: Server = { failOn: (url) => url.includes("cursor=c1"), currencies: [] };
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
            currencies: [],
        });

        render(<HistoryApp />);

        expect(await screen.findByText("#42")).toBeInTheDocument();
        expect(screen.getByText("#1")).toBeInTheDocument();
    });

    describe("filters", () => {
        const members: GroupMember[] = [
            { id: 42, firstName: "Ada", lastName: "Lovelace", username: null },
            { id: 1, firstName: "Grace", lastName: "Hopper", username: null },
        ];

        it("doesn't search on every keystroke, but once the typing pauses", async () => {
            const fetchMock = mockFetch([makeTransaction({ title: "Groceries" })]);

            render(<HistoryApp />);
            await screen.findByText("Groceries");

            fireEvent.change(screen.getByLabelText("filters.title"), { target: { value: "ren" } });
            fireEvent.change(screen.getByLabelText("filters.title"), { target: { value: "rent" } });

            expect(lastSearch(fetchMock).has("title")).toBe(false);
            await waitFor(() => expect(lastSearch(fetchMock).get("title")).toBe("rent"));
            expect(fetchMock.mock.calls.filter(([url]) => String(url).includes("title="))).toHaveLength(1);
        });

        it("replaces the loaded transactions with the matching ones", async () => {
            mockFetch((query) =>
                query.get("title") === "rent"
                    ? [makeTransaction({ id: 2, title: "Rent" })]
                    : [makeTransaction({ id: 1, title: "Groceries" })],
            );

            render(<HistoryApp />);
            await screen.findByText("Groceries");

            fireEvent.change(screen.getByLabelText("filters.title"), { target: { value: "rent" } });

            expect(await screen.findByText("Rent")).toBeInTheDocument();
            expect(screen.queryByText("Groceries")).not.toBeInTheDocument();
        });

        it("says that nothing matches when a filtered search finds nothing", async () => {
            mockFetch((query) => (query.has("title") ? [] : [makeTransaction({ title: "Groceries" })]));

            render(<HistoryApp />);
            await screen.findByText("Groceries");

            fireEvent.change(screen.getByLabelText("filters.title"), { target: { value: "nothing" } });

            expect(await screen.findByText("history.noMatches")).toBeInTheDocument();
        });

        it("filters by the participants picked among the members", async () => {
            const fetchMock = mockFetch([makeTransaction()], members);

            render(<HistoryApp />);
            fireEvent.click(await screen.findByLabelText("Ada Lovelace"));
            fireEvent.click(screen.getByLabelText("Grace Hopper"));

            await waitFor(() => expect(lastSearch(fetchMock).getAll("participant")).toEqual(["42", "1"]));

            fireEvent.click(screen.getByLabelText("Ada Lovelace"));

            await waitFor(() => expect(lastSearch(fetchMock).getAll("participant")).toEqual(["1"]));
        });

        it("offers the group's currencies and filters by the chosen one", async () => {
            const fetchMock = mockFetch([makeTransaction()], [], { failOn: () => false, currencies: ["EUR", "USD"] });

            render(<HistoryApp />);
            await screen.findByRole("option", { name: "USD" });

            fireEvent.change(screen.getByLabelText("filters.currency"), { target: { value: "USD" } });

            await waitFor(() => expect(lastSearch(fetchMock).getAll("currency")).toEqual(["USD"]));
        });

        it("allows an amount range only for a chosen currency", async () => {
            const fetchMock = mockFetch([makeTransaction()], [], { failOn: () => false, currencies: ["EUR"] });

            render(<HistoryApp />);
            await screen.findByRole("option", { name: "EUR" });

            expect(screen.getByLabelText("filters.amountMin")).toBeDisabled();
            expect(screen.getByLabelText("filters.amountMax")).toBeDisabled();

            fireEvent.change(screen.getByLabelText("filters.currency"), { target: { value: "EUR" } });
            fireEvent.change(screen.getByLabelText("filters.amountMin"), { target: { value: "5" } });
            fireEvent.change(screen.getByLabelText("filters.amountMax"), { target: { value: "10.5" } });

            await waitFor(() => expect(lastSearch(fetchMock).get("amountMax")).toBe("10.5"));
            expect(lastSearch(fetchMock).get("amountMin")).toBe("5");
            expect(lastSearch(fetchMock).get("currency")).toBe("EUR");

            fireEvent.change(screen.getByLabelText("filters.currency"), { target: { value: "" } });

            await waitFor(() => expect(lastSearch(fetchMock).has("amountMax")).toBe(false));
            expect(lastSearch(fetchMock).has("amountMin")).toBe(false);
            expect(screen.getByLabelText("filters.amountMin")).toBeDisabled();
        });

        it("filters by whole days", async () => {
            const fetchMock = mockFetch([makeTransaction()]);

            render(<HistoryApp />);
            await screen.findByText("Groceries");

            fireEvent.change(screen.getByLabelText("filters.dateFrom"), { target: { value: "2024-01-05" } });
            fireEvent.change(screen.getByLabelText("filters.dateTo"), { target: { value: "2024-01-06" } });

            await waitFor(() => expect(lastSearch(fetchMock).has("dateTo")).toBe(true));
            expect(new Date(lastSearch(fetchMock).get("dateFrom")!).getTime()).toBe(new Date(2024, 0, 5).getTime());
            expect(new Date(lastSearch(fetchMock).get("dateTo")!).getTime()).toBe(
                new Date(2024, 0, 6, 23, 59, 59, 999).getTime(),
            );
        });
    });
});

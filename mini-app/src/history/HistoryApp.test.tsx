import { render, screen, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { GroupMember } from "../common/api";
import { membersCache } from "../common/members";
import { Transaction } from "../common/transaction/Transaction";
import HistoryApp from "./HistoryApp";

const webApp = vi.hoisted(() => ({
    initData: "init-data-string",
    initDataUnsafe: { start_param: "token-123" },
}));

vi.mock("@twa-dev/sdk", () => ({
    default: webApp,
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

function mockFetch(transactions: Transaction[], members: GroupMember[] = []) {
    const fetchMock = vi.fn().mockImplementation((url: string) => {
        const body = url === "/app/api/group/members" ? { id: 1, members } : { transactions, nextCursor: null };

        return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(body) });
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

describe("HistoryApp", () => {
    afterEach(() => {
        vi.unstubAllGlobals();
        vi.clearAllMocks();
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
});

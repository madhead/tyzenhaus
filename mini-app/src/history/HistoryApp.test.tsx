import { render, screen, waitFor } from "@testing-library/react";
import { afterEach, describe, expect, it, vi } from "vitest";
import { Transaction } from "../common/transaction/Transaction";
import HistoryApp from "./HistoryApp";

const webApp = vi.hoisted(() => ({
    initData: "init-data-string",
    initDataUnsafe: { start_param: "token-123" },
}));

vi.mock("@twa-dev/sdk", () => ({
    default: webApp,
}));

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

function mockFetch(transactions: Transaction[]) {
    const fetchMock = vi.fn().mockResolvedValue({
        json: () => Promise.resolve(transactions),
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

describe("HistoryApp", () => {
    afterEach(() => {
        vi.unstubAllGlobals();
        vi.clearAllMocks();
    });

    it("requests the transactions with the bearer token", async () => {
        const fetchMock = mockFetch([]);

        render(<HistoryApp />);

        await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(1));
        expect(fetchMock).toHaveBeenCalledWith("/app/api/group/transactions", {
            method: "GET",
            headers: {
                "Authorization": "Bearer token-123",
                "X-Telegram-Init-Data": "init-data-string",
            },
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

    it("renders no cards when the history is empty", async () => {
        const fetchMock = mockFetch([]);

        const { container } = render(<HistoryApp />);

        await waitFor(() => expect(fetchMock).toHaveBeenCalled());
        expect(container.querySelectorAll(".transaction")).toHaveLength(0);
    });
});

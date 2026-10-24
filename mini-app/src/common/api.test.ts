import { afterEach, describe, expect, it, vi } from "vitest";
import { getCurrencies, getMembers, GroupMembers, searchTransactions, TransactionsPage } from "./api";

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

function mockFetch(body: unknown, ok = true, status = 200) {
    const fetchMock = vi.fn().mockResolvedValue({
        ok,
        status,
        json: () => Promise.resolve(body),
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

describe("api", () => {
    afterEach(() => {
        vi.unstubAllGlobals();
        vi.clearAllMocks();
    });

    describe("getMembers", () => {
        it("requests the group members with the auth headers and returns the parsed JSON", async () => {
            const body: GroupMembers = {
                id: 1,
                members: [{ id: 42, firstName: "Ada", lastName: "Lovelace", username: null }],
            };
            const fetchMock = mockFetch(body);

            const result = await getMembers();

            expect(fetchMock).toHaveBeenCalledWith("/app/api/group/members", {
                method: "GET",
                headers: expectedHeaders,
            });
            expect(result).toEqual(body);
        });

        it("normalizes an omitted members key to an empty array", async () => {
            mockFetch({ id: 1 });

            const result = await getMembers();

            expect(result).toEqual({ id: 1, members: [] });
        });

        it("throws naming the endpoint and status on a non-ok response", async () => {
            mockFetch(undefined, false, 401);

            await expect(getMembers()).rejects.toThrow(/group\/members/);
            await expect(getMembers()).rejects.toThrow(/401/);
        });
    });

    describe("getCurrencies", () => {
        it("requests the group currencies with the auth headers and returns the parsed JSON", async () => {
            const body = ["EUR", "USD"];
            const fetchMock = mockFetch(body);

            const result = await getCurrencies();

            expect(fetchMock).toHaveBeenCalledWith("/app/api/group/currencies", {
                method: "GET",
                headers: expectedHeaders,
            });
            expect(result).toEqual(body);
        });

        it("throws naming the endpoint and status on a non-ok response", async () => {
            mockFetch(undefined, false, 401);

            await expect(getCurrencies()).rejects.toThrow(/group\/currencies/);
            await expect(getCurrencies()).rejects.toThrow(/401/);
        });
    });

    describe("searchTransactions", () => {
        it("requests a bare URL when no params are given", async () => {
            const body: TransactionsPage = { transactions: [], nextCursor: null };
            const fetchMock = mockFetch(body);

            const result = await searchTransactions();

            expect(fetchMock).toHaveBeenCalledWith("/app/api/group/transactions", {
                method: "GET",
                headers: expectedHeaders,
            });
            expect(result).toEqual(body);
        });

        it("requests a bare URL when called with no arguments at all and undefined/empty params are omitted", async () => {
            const fetchMock = mockFetch({ transactions: [], nextCursor: null });

            await searchTransactions({
                title: "",
                participants: [],
                amountMin: undefined,
                amountMax: undefined,
                currencies: [],
                dateFrom: undefined,
                dateTo: undefined,
                cursor: undefined,
                limit: undefined,
            });

            expect(fetchMock).toHaveBeenCalledWith("/app/api/group/transactions", {
                method: "GET",
                headers: expectedHeaders,
            });
        });

        it("includes every param, using the exact backend names, when all are set", async () => {
            const fetchMock = mockFetch({ transactions: [], nextCursor: null });

            await searchTransactions({
                title: "Groceries",
                participants: [1, 2],
                amountMin: "10.00",
                amountMax: "100.00",
                currencies: ["EUR", "USD"],
                dateFrom: "2024-01-01T00:00:00Z",
                dateTo: "2024-12-31T23:59:59Z",
                cursor: "opaque-cursor",
                limit: 25,
            });

            const calledUrl = fetchMock.mock.calls[0][0] as string;
            const [path, queryString] = calledUrl.split("?");
            const params = new URLSearchParams(queryString);

            expect(path).toBe("/app/api/group/transactions");
            expect(params.get("title")).toBe("Groceries");
            expect(params.getAll("participant")).toEqual(["1", "2"]);
            expect(params.get("amountMin")).toBe("10.00");
            expect(params.get("amountMax")).toBe("100.00");
            expect(params.getAll("currency")).toEqual(["EUR", "USD"]);
            expect(params.get("dateFrom")).toBe("2024-01-01T00:00:00Z");
            expect(params.get("dateTo")).toBe("2024-12-31T23:59:59Z");
            expect(params.get("cursor")).toBe("opaque-cursor");
            expect(params.get("limit")).toBe("25");
        });

        it("appends one participant entry per element", async () => {
            const fetchMock = mockFetch({ transactions: [], nextCursor: null });

            await searchTransactions({ participants: [1, 2, 3] });

            const calledUrl = fetchMock.mock.calls[0][0] as string;
            const params = new URLSearchParams(calledUrl.split("?")[1]);

            expect(params.getAll("participant")).toEqual(["1", "2", "3"]);
        });

        it("appends one currency entry per element", async () => {
            const fetchMock = mockFetch({ transactions: [], nextCursor: null });

            await searchTransactions({ currencies: ["EUR", "USD", "GBP"] });

            const calledUrl = fetchMock.mock.calls[0][0] as string;
            const params = new URLSearchParams(calledUrl.split("?")[1]);

            expect(params.getAll("currency")).toEqual(["EUR", "USD", "GBP"]);
        });

        it("returns the parsed JSON page", async () => {
            const body: TransactionsPage = {
                transactions: [
                    {
                        id: 1,
                        payer: 42,
                        recipients: [1, 2],
                        amount: "10.00",
                        currency: "EUR",
                        title: "Groceries",
                        timestamp: Date.UTC(2024, 0, 5, 12, 0, 0),
                    },
                ],
                nextCursor: "next-page",
            };
            mockFetch(body);

            const result = await searchTransactions();

            expect(result).toEqual(body);
        });

        it("normalizes an omitted nextCursor key to null", async () => {
            mockFetch({ transactions: [] });

            const result = await searchTransactions();

            expect(result).toEqual({ transactions: [], nextCursor: null });
        });

        it("throws naming the endpoint and status on a non-ok response", async () => {
            mockFetch(undefined, false, 401);

            await expect(searchTransactions()).rejects.toThrow(/group\/transactions/);
            await expect(searchTransactions()).rejects.toThrow(/401/);
        });
    });
});

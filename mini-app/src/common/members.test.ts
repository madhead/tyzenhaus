import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { GroupMember } from "./api";
import { Members, MembersCache } from "./members";

const webApp = vi.hoisted(() => ({
    initData: "init-data-string",
    initDataUnsafe: { start_param: "token-123" },
}));

vi.mock("@twa-dev/sdk", () => ({
    default: webApp,
}));

function mockFetch(members: GroupMember[]) {
    const fetchMock = vi.fn().mockResolvedValue({
        ok: true,
        status: 200,
        json: () => Promise.resolve({ id: 1, members }),
    });
    vi.stubGlobal("fetch", fetchMock);
    return fetchMock;
}

describe("Members", () => {
    it("joins first and last name with a space", () => {
        const members = new Members([{ id: 1, firstName: "Ada", lastName: "Lovelace", username: "ada" }]);

        expect(members.name(1)).toBe("Ada Lovelace");
    });

    it("skips blank name parts", () => {
        const members = new Members([
            { id: 1, firstName: "Ada", lastName: "   ", username: "ada" },
            { id: 2, firstName: "", lastName: "Lovelace", username: "ada" },
        ]);

        expect(members.name(1)).toBe("Ada");
        expect(members.name(2)).toBe("Lovelace");
    });

    it("resolves the name from the remaining part when firstName is missing", () => {
        const members = new Members([{ id: 1, lastName: "Lovelace", username: "ada" } as unknown as GroupMember]);

        expect(members.name(1)).toBe("Lovelace");
    });

    it("resolves the name from the remaining part when lastName is null", () => {
        const members = new Members([
            { id: 1, firstName: "Ada", lastName: null, username: "ada" } as unknown as GroupMember,
        ]);

        expect(members.name(1)).toBe("Ada");
    });

    it("falls back to the username when both name parts are blank", () => {
        const members = new Members([{ id: 1, firstName: "", lastName: "  ", username: "ada" }]);

        expect(members.name(1)).toBe("ada");
    });

    it("falls back to #id when name parts are blank and there is no username", () => {
        const members = new Members([{ id: 7, firstName: "", lastName: "", username: null }]);

        expect(members.name(7)).toBe("#7");
    });

    it("falls back to #id for an id missing from the roster", () => {
        const members = new Members([{ id: 1, firstName: "Ada", lastName: "Lovelace", username: null }]);

        expect(members.name(99)).toBe("#99");
    });
});

describe("MembersCache", () => {
    let cache: MembersCache;

    beforeEach(() => {
        cache = new MembersCache();
    });

    afterEach(() => {
        vi.unstubAllGlobals();
        vi.clearAllMocks();
    });

    it("resolves member names from the fetched roster", async () => {
        mockFetch([{ id: 1, firstName: "Ada", lastName: "Lovelace", username: null }]);

        const members = await cache.get();

        expect(members.name(1)).toBe("Ada Lovelace");
    });

    it("fetches once and reuses the cache on later calls", async () => {
        const fetchMock = mockFetch([{ id: 1, firstName: "Ada", lastName: "Lovelace", username: null }]);

        await cache.get();
        await cache.get();

        expect(fetchMock).toHaveBeenCalledTimes(1);
    });

    it("shares a single fetch between concurrent callers", async () => {
        const fetchMock = mockFetch([{ id: 1, firstName: "Ada", lastName: "Lovelace", username: null }]);

        const [first, second] = await Promise.all([cache.get(), cache.get()]);

        expect(fetchMock).toHaveBeenCalledTimes(1);
        expect(first).toBe(second);
    });

    it("does not cache a failed fetch and refetches on retry", async () => {
        const fetchMock = vi
            .fn()
            .mockResolvedValueOnce({ ok: false, status: 500, json: () => Promise.resolve(undefined) })
            .mockResolvedValueOnce({
                ok: true,
                status: 200,
                json: () =>
                    Promise.resolve({
                        id: 1,
                        members: [{ id: 1, firstName: "Ada", lastName: "Lovelace", username: null }],
                    }),
            });
        vi.stubGlobal("fetch", fetchMock);

        await expect(cache.get()).rejects.toThrow();

        const members = await cache.get();

        expect(fetchMock).toHaveBeenCalledTimes(2);
        expect(members.name(1)).toBe("Ada Lovelace");
    });

    it("refetches after the cache is reset", async () => {
        const fetchMock = mockFetch([{ id: 1, firstName: "Ada", lastName: "Lovelace", username: null }]);

        await cache.get();
        cache.reset();
        await cache.get();

        expect(fetchMock).toHaveBeenCalledTimes(2);
    });
});

import { describe, expect, it } from "vitest";
import { Filters, isFiltered, NO_FILTERS, toSearchParams } from "./filters";

function filters(overrides: Partial<Filters>): Filters {
    return { ...NO_FILTERS, ...overrides };
}

describe("toSearchParams", () => {
    it("leaves everything out when nothing is filled in", () => {
        const params = toSearchParams(NO_FILTERS);

        expect(params).toEqual({ participants: [], currencies: [] });
        expect(isFiltered(params)).toBe(false);
    });

    it("trims the title and ignores a blank one", () => {
        expect(toSearchParams(filters({ title: "  rent " })).title).toBe("rent");
        expect(toSearchParams(filters({ title: "   " })).title).toBeUndefined();
    });

    it("passes participants and the currency through", () => {
        const params = toSearchParams(filters({ participants: [1, 2], currency: "EUR" }));

        expect(params.participants).toEqual([1, 2]);
        expect(params.currencies).toEqual(["EUR"]);
    });

    it("applies the amount range only together with a currency", () => {
        const withCurrency = toSearchParams(filters({ currency: "EUR", amountMin: "5", amountMax: "10.5" }));
        const withoutCurrency = toSearchParams(filters({ amountMin: "5", amountMax: "10.5" }));

        expect(withCurrency.amountMin).toBe("5");
        expect(withCurrency.amountMax).toBe("10.5");
        expect(withoutCurrency.amountMin).toBeUndefined();
        expect(withoutCurrency.amountMax).toBeUndefined();
    });

    it("turns dates into the start and the end of the day", () => {
        const params = toSearchParams(filters({ dateFrom: "2024-01-05", dateTo: "2024-01-06" }));

        expect(new Date(params.dateFrom!).getTime()).toBe(new Date(2024, 0, 5, 0, 0, 0, 0).getTime());
        expect(new Date(params.dateTo!).getTime()).toBe(new Date(2024, 0, 6, 23, 59, 59, 999).getTime());
    });
});

describe("isFiltered", () => {
    it("is true as soon as any filter is set", () => {
        expect(isFiltered(toSearchParams(filters({ title: "rent" })))).toBe(true);
        expect(isFiltered(toSearchParams(filters({ participants: [1] })))).toBe(true);
        expect(isFiltered(toSearchParams(filters({ currency: "EUR" })))).toBe(true);
        expect(isFiltered(toSearchParams(filters({ dateTo: "2024-01-06" })))).toBe(true);
    });
});

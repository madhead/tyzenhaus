import { TransactionsSearchParams } from "../common/api";
import dayjs from "../datetime";

/**
 * What the filter bar edits. Everything is a string (or a list of IDs), exactly as the form controls hold it.
 */
export type Filters = {
    title: string;
    participants: number[];
    currency: string;
    amountMin: string;
    amountMax: string;
    dateFrom: string;
    dateTo: string;
};

export const NO_FILTERS: Filters = {
    title: "",
    participants: [],
    currency: "",
    amountMin: "",
    amountMax: "",
    dateFrom: "",
    dateTo: "",
};

/**
 * How long to wait after the last edit before searching.
 */
export const SEARCH_DEBOUNCE_MS = 300;

/**
 * Converts the form state into search parameters.
 *
 * Amounts of different currencies aren't comparable, so an amount range only applies together with a currency. Dates are
 * whole days in the user's time zone: from the start of the first one to the end of the last one.
 */
export function toSearchParams(filters: Filters): TransactionsSearchParams {
    return {
        title: filters.title.trim() || undefined,
        participants: filters.participants,
        currencies: filters.currency ? [filters.currency] : [],
        amountMin: filters.currency ? filters.amountMin || undefined : undefined,
        amountMax: filters.currency ? filters.amountMax || undefined : undefined,
        dateFrom: filters.dateFrom ? dayjs(filters.dateFrom).startOf("day").toISOString() : undefined,
        dateTo: filters.dateTo ? dayjs(filters.dateTo).endOf("day").toISOString() : undefined,
    };
}

/**
 * Whether the parameters narrow the search down at all.
 */
export function isFiltered(params: TransactionsSearchParams): boolean {
    return Object.values(params).some((value) => (Array.isArray(value) ? value.length > 0 : value !== undefined));
}

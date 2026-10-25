import { useCallback, useEffect, useRef, useState } from "react";
import { searchTransactions, TransactionsSearchParams } from "../common/api";
import { Transaction } from "../common/transaction/Transaction";

/**
 * Pages through the group's transactions matching the [params], newest first, accumulating them as more pages are loaded.
 *
 * The [params] are taken once: to search for something else, mount the hook anew (e.g. by changing the `key` of the
 * component that uses it), which also drops everything loaded so far.
 *
 * Whether a request is in flight and whether the last one failed are tracked separately from the transactions, because
 * the UI treats the first page (nothing to show yet) differently from the following ones (keep showing what's loaded).
 */
export function useTransactions(params: TransactionsSearchParams) {
    const [searchParams] = useState(params);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [nextCursor, setNextCursor] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [failed, setFailed] = useState(false);
    const loadingRef = useRef(false);

    const load = useCallback(
        async (cursor?: string) => {
            if (loadingRef.current) {
                return;
            }

            loadingRef.current = true;
            setLoading(true);
            setFailed(false);

            try {
                const page = await searchTransactions({ ...searchParams, cursor });

                setTransactions((loaded) => [...loaded, ...page.transactions]);
                setNextCursor(page.nextCursor);
            } catch (error) {
                console.error("Failed to load transactions", error);
                setFailed(true);
            } finally {
                loadingRef.current = false;
                setLoading(false);
            }
        },
        [searchParams],
    );

    useEffect(() => {
        void load();
    }, [load]);

    const loadMore = useCallback(() => {
        void load(nextCursor ?? undefined);
    }, [load, nextCursor]);

    return { transactions, nextCursor, loading, failed, loadMore };
}

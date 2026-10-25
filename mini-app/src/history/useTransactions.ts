import { useCallback, useEffect, useRef, useState } from "react";
import { searchTransactions } from "../common/api";
import { Transaction } from "../common/transaction/Transaction";

/**
 * Pages through the group's transactions, newest first, accumulating them as more pages are loaded.
 *
 * Whether a request is in flight and whether the last one failed are tracked separately from the transactions, because
 * the UI treats the first page (nothing to show yet) differently from the following ones (keep showing what's loaded).
 */
export function useTransactions() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [nextCursor, setNextCursor] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);
    const [failed, setFailed] = useState(false);
    const loadingRef = useRef(false);

    const load = useCallback(async (cursor?: string) => {
        if (loadingRef.current) {
            return;
        }

        loadingRef.current = true;
        setLoading(true);
        setFailed(false);

        try {
            const page = await searchTransactions({ cursor });

            setTransactions((loaded) => [...loaded, ...page.transactions]);
            setNextCursor(page.nextCursor);
        } catch (error) {
            console.error("Failed to load transactions", error);
            setFailed(true);
        } finally {
            loadingRef.current = false;
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        void load();
    }, [load]);

    const loadMore = useCallback(() => {
        void load(nextCursor ?? undefined);
    }, [load, nextCursor]);

    return { transactions, nextCursor, loading, failed, loadMore };
}

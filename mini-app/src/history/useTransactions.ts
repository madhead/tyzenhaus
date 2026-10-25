import { useCallback, useEffect, useRef, useState } from "react";
import { searchTransactions } from "../common/api";
import { Transaction } from "../common/transaction/Transaction";

/**
 * Pages through the group's transactions, newest first, accumulating them as more pages are loaded.
 */
export function useTransactions() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [nextCursor, setNextCursor] = useState<string | null>(null);
    const loadingRef = useRef(false);

    const load = useCallback(async (cursor?: string) => {
        if (loadingRef.current) {
            return;
        }

        loadingRef.current = true;

        try {
            const page = await searchTransactions({ cursor });

            setTransactions((loaded) => [...loaded, ...page.transactions]);
            setNextCursor(page.nextCursor);
        } finally {
            loadingRef.current = false;
        }
    }, []);

    useEffect(() => {
        void load();
    }, [load]);

    const loadMore = useCallback(() => {
        if (nextCursor) {
            void load(nextCursor);
        }
    }, [load, nextCursor]);

    return { transactions, nextCursor, loadMore };
}

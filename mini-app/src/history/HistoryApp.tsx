import "./HistoryApp.less";

import { useEffect, useRef, useState } from "react";
import { Members, membersCache } from "../common/members";
import TransactionCard from "../common/transaction/Transaction";
import { useTransactions } from "./useTransactions";

function HistoryApp() {
    const { transactions, nextCursor, loadMore } = useTransactions();
    const [members, setMembers] = useState<Members>(() => new Members());

    useEffect(() => {
        void membersCache.get().then(setMembers);
    }, []);

    return (
        <main aria-label="Transaction history">
            {/* eslint-disable-next-line jsx-a11y-x/no-redundant-roles */}
            <ul className="history" role="list">
                {transactions.map((transaction) => (
                    <li key={transaction.id}>
                        <TransactionCard transaction={transaction} members={members} />
                    </li>
                ))}
            </ul>
            {nextCursor && <Sentinel key={nextCursor} onVisible={loadMore} />}
        </main>
    );
}

/**
 * An invisible marker at the end of the list. Calls [onVisible] as soon as it scrolls into view (or right away, if the
 * loaded page didn't fill the screen).
 */
function Sentinel({ onVisible }: { onVisible: () => void }) {
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const element = ref.current;

        if (!element) {
            return;
        }

        const observer = new IntersectionObserver((entries) => {
            if (entries.some((entry) => entry.isIntersecting)) {
                onVisible();
            }
        });

        observer.observe(element);

        return () => {
            observer.disconnect();
        };
    }, [onVisible]);

    return <div ref={ref} className="sentinel" aria-hidden="true" />;
}

export default HistoryApp;

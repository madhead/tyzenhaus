import "./HistoryApp.less";

import { useEffect, useState } from "react";
import { searchTransactions } from "../common/api";
import { Members, membersCache } from "../common/members";
import TransactionCard, { Transaction } from "../common/transaction/Transaction";

function HistoryApp() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [members, setMembers] = useState<Members>(new Members());

    useEffect(() => {
        async function loadHistory() {
            const [page, groupMembers] = await Promise.all([searchTransactions(), membersCache.get()]);

            setTransactions(page.transactions);
            setMembers(groupMembers);
        }

        void loadHistory();
    }, []);

    return (
        <main aria-label="Transaction history">
            {/* eslint-disable-next-line jsx-a11y/no-redundant-roles */}
            <ul className="history" role="list">
                {transactions.map((transaction) => (
                    <li key={transaction.id}>
                        <TransactionCard transaction={transaction} members={members} />
                    </li>
                ))}
            </ul>
        </main>
    );
}

export default HistoryApp;

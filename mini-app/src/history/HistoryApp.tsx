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
        <div className="history">
            {transactions.map((transaction) => (
                <TransactionCard key={transaction.id} transaction={transaction} members={members} />
            ))}
        </div>
    );
}

export default HistoryApp;

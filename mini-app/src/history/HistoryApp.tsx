import "./HistoryApp.less";

import { useEffect, useState } from "react";
import { searchTransactions } from "../common/api";
import TransactionCard, { Transaction } from "../common/transaction/Transaction";

function HistoryApp() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);

    useEffect(() => {
        async function loadTransactions() {
            const page = await searchTransactions();

            setTransactions(page.transactions);
        }

        void loadTransactions();
    }, []);

    return (
        <div className="history">
            {transactions.map((transaction) => (
                <TransactionCard key={transaction.id} {...transaction} />
            ))}
        </div>
    );
}

export default HistoryApp;

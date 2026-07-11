import "./HistoryApp.less";

import { useEffect, useState } from "react";
import { authHeaders } from "../common/api";
import TransactionCard, { Transaction } from "../common/transaction/Transaction";

function HistoryApp() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);

    useEffect(() => {
        async function loadTransactions() {
            const response = await fetch("/app/api/group/transactions", {
                method: "GET",
                headers: authHeaders(),
            });

            setTransactions((await response.json()) as Transaction[]);
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

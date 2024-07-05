import "./HistoryApp.less";

import WebApp from "@twa-dev/sdk";
import { useEffect, useState } from "react";
import TransactionCard, {
  Transaction,
} from "../common/transaction/Transaction";
import { ParticipantsProvider } from "../common/parricipants/participants";
import { CurrenciesProvider } from "../common/currencies/currencies";

function HistoryApp() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  useEffect(() => {
    async function loadTransactions() {
      let response = await fetch("/app/api/group/transactions", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${WebApp.initDataUnsafe.start_param}`,
        },
      });

      setTransactions((await response.json()).slice(0, 10));
    }

    loadTransactions();
  }, []);

  return (
    <ParticipantsProvider>
      <CurrenciesProvider>
        <div className="history">
          {transactions.map((transaction) => (
            <TransactionCard key={transaction.id} {...transaction} />
          ))}
        </div>
      </CurrenciesProvider>
    </ParticipantsProvider>
  );
}

export default HistoryApp;

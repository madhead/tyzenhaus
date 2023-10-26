import "./HistoryApp.less";

import WebApp from "@twa-dev/sdk";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import TransactionCard, {
  Transaction,
} from "../common/transaction/Transaction";

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

      setTransactions(await response.json());
    }

    loadTransactions();
  }, []);

  const { t } = useTranslation();

  return (
    <div className="history">
      {transactions.map((transaction) => (
        <TransactionCard key={transaction.id} {...transaction} />
      ))}
    </div>
  );
}

export default HistoryApp;

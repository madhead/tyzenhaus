import moment from "moment";
import Marquee from "../marquee/Marquee";
import { participantName, useParticipants } from "../parricipants/participants";
import "./Transaction.less";
import { useTranslation } from "react-i18next";

export type Transaction = {
  id: number;
  payer: number;
  recipients: Set<number>;
  amount: string;
  currency: string;
  title: string;
  timestamp: number;
};

export default function TransactionCard(transaction: Transaction) {
  return (
    <div className="transaction">
      <div className="info">
        <Timestamp timestamp={transaction.timestamp} />
        <Title title={transaction.title} />
        <Amount amount={transaction.amount} currency={transaction.currency} />
      </div>
      <div className="participants">
        <Participants {...transaction} />
      </div>
    </div>
  );
}

function Timestamp({ timestamp }: { timestamp: number }) {
  const m = moment(timestamp);

  return (
    <div className="timestamp" title={m.format("llll")}>
      <div className="month">{m.format("MMM")}</div>
      <div className="date">{m.format("DD")}</div>
    </div>
  );
}

function Title({ title }: { title: string }) {
  return (
    <div className="title">
      <Marquee>{title}</Marquee>
    </div>
  );
}

function Amount({ amount, currency }: { amount: string; currency: string }) {
  function formatAmount(amount: string) {
    let result = parseFloat(amount).toFixed(2);

    while (result[result.length - 1] === "0") {
      result = result.slice(0, result.length - 1);
    }

    if (result[result.length - 1] === ".") {
      result = result.slice(0, result.length - 1);
    }

    return result;
  }

  return (
    <div className="amount">
      <div className="currency">{currency}</div>
      <div className="quantity">{formatAmount(amount)}</div>
    </div>
  );
}

function Participants({
  payer,
  recipients,
}: {
  payer: number;
  recipients: Set<number>;
}) {
  let participants = useParticipants() || [];
  const payerParticipant = participants?.find((p) => p.id === payer);
  const recipientParticipants = Array.from(recipients).map((r: number) =>
    participantName(r, participants)
  );
  const { t } = useTranslation();

  return (
    <>
      {t("history.paidFor", {
        payer: `${participantName(payer, participants)}`,
        recipients: oxford(recipientParticipants, t("history.and")),
      })}
    </>
  );
}

function oxford(arr: any[], conjunction: string): string {
  let l = arr.length;
  if (l < 2) return arr[0];
  if (l < 3) return arr.join(` ${conjunction} `);
  arr = arr.slice();
  arr[l - 1] = `${conjunction} ${arr[l - 1]}`;
  return arr.join(", ");
}

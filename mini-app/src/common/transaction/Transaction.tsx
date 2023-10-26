import moment from "moment";
import { useEffect, useRef, useState } from "react";
import Marquee from "react-fast-marquee";
import "./Transaction.less";

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
      <div className="participants"></div>
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
  const titleContainerRef = useRef<HTMLDivElement>(null);
  const [marquee, setMarquee] = useState(false);

  useEffect(() => {
    if (!titleContainerRef.current) {
      return;
    }

    const element = titleContainerRef.current;

    if (
      element.offsetWidth < element.scrollWidth ||
      element.offsetHeight < element.scrollHeight
    ) {
      setMarquee(true);
    }
  }, [titleContainerRef]);

  return (
    <div className="title" ref={titleContainerRef}>
      {marquee && (
        <Marquee delay={3}>
          {title}
          <span className="spacer" />
        </Marquee>
      )}
      {!marquee && <span>{title}</span>}
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

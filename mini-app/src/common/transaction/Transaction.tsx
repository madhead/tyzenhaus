import { useEffect, useRef, useState } from "react";
import dayjs from "../../datetime";
import { Members } from "../members";
import "./Transaction.less";

export type Transaction = {
    id: number;
    payer: number;
    recipients: number[];
    amount: string;
    currency: string;
    title: string;
    timestamp: number;
};

export default function TransactionCard({ transaction, members }: { transaction: Transaction; members: Members }) {
    return (
        <div className="transaction">
            <div className="info">
                <Timestamp timestamp={transaction.timestamp} />
                <Title title={transaction.title} />
                <Amount amount={transaction.amount} currency={transaction.currency} />
            </div>
            <Participants payer={transaction.payer} recipients={transaction.recipients} members={members} />
        </div>
    );
}

function Timestamp({ timestamp }: { timestamp: number }) {
    const m = dayjs(timestamp);

    return (
        <div className="timestamp tooltip" title={fullDateTime(timestamp)}>
            <div className="month">{m.format("MMM")}</div>
            <div className="date">{m.format("DD")}</div>
        </div>
    );
}

function fullDateTime(timestamp: number): string {
    const locale = document.documentElement.lang || "en";

    // Weekday/date presentation follows the locale, but the time is always 24-hour (`h23`) — never AM/PM,
    // regardless of what the locale would otherwise pick.
    return new Intl.DateTimeFormat(locale, {
        weekday: "short",
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hourCycle: "h23",
    }).format(timestamp);
}

function Title({ title }: { title: string }) {
    const textRef = useRef<HTMLDivElement>(null);
    const [truncated, setTruncated] = useState(false);

    // Only titles the ellipsis actually clips get a tooltip; a fully-visible title has nothing extra to reveal.
    // Overflow can only be measured, not expressed in CSS, so watch the element for size changes.
    useEffect(() => {
        const element = textRef.current;

        if (!element) {
            return;
        }

        const measure = () => setTruncated(element.scrollWidth > element.clientWidth);

        measure();

        const observer = new ResizeObserver(measure);

        observer.observe(element);

        return () => observer.disconnect();
    }, [title]);

    // The `.text` child is the single-line, ellipsis-truncated part; when clipped, the full title lives on the
    // outer element's `title` attribute — shown natively on desktop hover and via the shared `.tooltip` popup on touch.
    return (
        <div className={truncated ? "title tooltip" : "title"} title={truncated ? title : undefined}>
            <div className="text" ref={textRef}>
                {title}
            </div>
        </div>
    );
}

function Amount({ amount, currency }: { amount: string; currency: string }) {
    return (
        <div className="amount">
            <div className="currency">{currency}</div>
            <div className="quantity">{formatAmount(amount, currency)}</div>
        </div>
    );
}

function Participants({ payer, recipients, members }: { payer: number; recipients: number[]; members: Members }) {
    const payerName = members.name(payer);
    const recipientNames = recipients.map((id) => members.name(id)).join(", ");

    return <div className="participants">{recipients.length > 0 ? `${payerName} → ${recipientNames}` : payerName}</div>;
}

function formatAmount(amount: string, currency: string): string {
    const value = parseFloat(amount);
    const locale = document.documentElement.lang || "en";

    try {
        // Currency-aware fraction digits (EUR → 2, JPY → 0) without repeating the code inside `.quantity`:
        // format with the ISO code display, then drop the `currency` part and any literal whitespace touching it.
        const parts = new Intl.NumberFormat(locale, {
            style: "currency",
            currency,
            currencyDisplay: "code",
        }).formatToParts(value);

        return parts
            .filter((part, index) => {
                if (part.type === "currency") {
                    return false;
                }

                if (part.type === "literal" && part.value.trim().length === 0) {
                    return parts[index - 1]?.type !== "currency" && parts[index + 1]?.type !== "currency";
                }

                return true;
            })
            .map((part) => part.value)
            .join("");
    } catch (error) {
        // Group currencies are not guaranteed to be valid ISO-4217 codes; `Intl.NumberFormat` throws
        // `RangeError` on those, so fall back to a plain localized decimal.
        if (error instanceof RangeError) {
            return new Intl.NumberFormat(locale).format(value);
        }

        throw error;
    }
}

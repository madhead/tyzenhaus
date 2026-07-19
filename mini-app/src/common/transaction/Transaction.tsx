import { useCallback, useLayoutEffect, useRef, useState } from "react";
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
        <article className="transaction">
            <div className="info">
                <Timestamp timestamp={transaction.timestamp} />
                <div className="details">
                    <Scrollable className="payer" text={members.name(transaction.payer)} />
                    <Scrollable className="title" text={transaction.title} />
                </div>
                <Amount amount={transaction.amount} currency={transaction.currency} />
            </div>
            <Recipients recipients={transaction.recipients} members={members} />
        </article>
    );
}

function Timestamp({ timestamp }: { timestamp: number }) {
    const m = dayjs(timestamp);

    return (
        <time className="timestamp" dateTime={m.format("YYYY-MM-DDTHH:mm")} title={m.format("YYYY-MM-DD HH:mm")}>
            <div className="month">{m.format("MMM")}</div>
            <div className="date">{m.format("DD")}</div>
        </time>
    );
}

/**
 * A single line of text that never wraps: plain when it fits, and when it doesn't, an ellipsis plus the full text as a
 * native tooltip on desktop, or a drag-scrollable line with edge arrows on touch (where there is no hover).
 */
function Scrollable({ className, text }: { className: string; text: string }) {
    const scrollerRef = useRef<HTMLDivElement>(null);
    const [overflowing, setOverflowing] = useState(false);
    const [canLeft, setCanLeft] = useState(false);
    const [canRight, setCanRight] = useState(false);

    const update = useCallback(() => {
        const element = scrollerRef.current;

        if (!element) {
            return;
        }

        const maxScroll = element.scrollWidth - element.clientWidth;

        setOverflowing(maxScroll > 0);
        setCanLeft(element.scrollLeft > 0);
        setCanRight(element.scrollLeft < maxScroll - 1);
    }, []);

    useLayoutEffect(() => {
        const element = scrollerRef.current;

        if (!element) {
            return;
        }

        update();

        const observer = new ResizeObserver(update);

        observer.observe(element);

        return () => observer.disconnect();
    }, [text, update]);

    return (
        <div className={overflowing ? `${className} scrollable` : className}>
            <div className="scroller" ref={scrollerRef} onScroll={update} title={overflowing ? text : undefined}>
                <span className="text">{text}</span>
            </div>
            {canLeft && (
                <span className="arrow left" aria-hidden="true">
                    ‹
                </span>
            )}
            {canRight && (
                <span className="arrow right" aria-hidden="true">
                    ›
                </span>
            )}
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

function Recipients({ recipients, members }: { recipients: number[]; members: Members }) {
    if (recipients.length === 0) {
        return null;
    }

    return (
        // eslint-disable-next-line jsx-a11y/no-redundant-roles
        <ul className="recipients" role="list">
            {recipients.map((id) => (
                <li key={id}>{members.name(id)}</li>
            ))}
        </ul>
    );
}

function formatAmount(amount: string, currency: string): string {
    const value = parseFloat(amount);
    const locale = document.documentElement.lang || "en";

    try {
        const parts = new Intl.NumberFormat(locale, {
            style: "currency",
            currency: currency,
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
        // Group currencies are not guaranteed to be valid currency codes; `NumberFormat` throws `RangeError` on those, so fall back to a plain localized decimal.
        if (error instanceof RangeError) {
            return new Intl.NumberFormat(locale).format(value);
        }

        throw error;
    }
}

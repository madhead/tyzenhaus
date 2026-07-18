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
        <div className="timestamp" title={m.format("YYYY-MM-DD HH:mm")}>
            <div className="month">{m.format("MMM")}</div>
            <div className="date">{m.format("DD")}</div>
        </div>
    );
}

function Title({ title }: { title: string }) {
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
    }, [title, update]);

    return (
        <div className={overflowing ? "title scrollable" : "title"}>
            <div className="scroller" ref={scrollerRef} onScroll={update} title={overflowing ? title : undefined}>
                <span className="text">{title}</span>
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

function Participants({ payer, recipients, members }: { payer: number; recipients: number[]; members: Members }) {
    const payerName = members.name(payer);
    const recipientNames = recipients.map((id) => members.name(id)).join(", ");

    return <div className="participants">{recipients.length > 0 ? `${payerName} → ${recipientNames}` : payerName}</div>;
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

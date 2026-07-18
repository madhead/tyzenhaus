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
        <div className="timestamp" title={m.format("llll")}>
            <div className="month">{m.format("MMM")}</div>
            <div className="date">{m.format("DD")}</div>
        </div>
    );
}

function Title({ title }: { title: string }) {
    const textRef = useRef<HTMLSpanElement>(null);
    const [expanded, setExpanded] = useState(false);
    const [truncatable, setTruncatable] = useState(false);

    // Overflow can only be measured. Detect it while the title is a single clipped line so we only offer the
    // tap-to-expand affordance (and animated ellipsis) when there's more to reveal; keep the last measurement
    // while expanded (the text wraps then, so there's no horizontal overflow to see).
    useEffect(() => {
        const element = textRef.current;

        if (!element) {
            return;
        }

        const measure = () => {
            if (!expanded) {
                setTruncatable(element.scrollWidth > element.clientWidth);
            }
        };

        measure();

        const observer = new ResizeObserver(measure);

        observer.observe(element);

        return () => observer.disconnect();
    }, [title, expanded]);

    const interactive = truncatable || expanded;
    const showEllipsis = truncatable && !expanded;

    const classes = ["title"];

    if (interactive) {
        classes.push("interactive");
    }

    if (expanded) {
        classes.push("expanded");
    }

    const toggle = () => setExpanded((value) => !value);

    return (
        <div
            className={classes.join(" ")}
            // While collapsed, the native tooltip / long-press still surfaces the full title on desktop.
            title={showEllipsis ? title : undefined}
            role={interactive ? "button" : undefined}
            tabIndex={interactive ? 0 : undefined}
            aria-expanded={interactive ? expanded : undefined}
            onClick={interactive ? toggle : undefined}
            onKeyDown={
                interactive
                    ? (event) => {
                          if (event.key === "Enter" || event.key === " ") {
                              event.preventDefault();
                              toggle();
                          }
                      }
                    : undefined
            }
        >
            <span className="text" ref={textRef}>
                {title}
            </span>
            {showEllipsis && (
                <span className="ellipsis" aria-hidden="true">
                    <i />
                    <i />
                    <i />
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

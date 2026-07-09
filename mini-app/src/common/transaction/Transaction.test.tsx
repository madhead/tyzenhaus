import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import dayjs from "../../datetime";
import TransactionCard, { Transaction } from "./Transaction";

function makeTransaction(overrides: Partial<Transaction> = {}): Transaction {
    return {
        id: 1,
        payer: 42,
        recipients: [1, 2],
        amount: "10.00",
        currency: "EUR",
        title: "Groceries",
        timestamp: Date.UTC(2024, 0, 5, 12, 0, 0),
        ...overrides,
    };
}

describe("TransactionCard", () => {
    it("renders the title", () => {
        render(<TransactionCard {...makeTransaction({ title: "Groceries" })} />);

        expect(screen.getByText("Groceries")).toBeInTheDocument();
    });

    it("renders the currency", () => {
        render(<TransactionCard {...makeTransaction({ currency: "USD" })} />);

        expect(screen.getByText("USD")).toBeInTheDocument();
    });

    describe("timestamp", () => {
        it("shows the abbreviated month and zero-padded day of the transaction", () => {
            const timestamp = Date.UTC(2024, 0, 5, 12, 0, 0);
            render(<TransactionCard {...makeTransaction({ timestamp })} />);

            const m = dayjs(timestamp);
            expect(screen.getByText(m.format("MMM"))).toBeInTheDocument();
            expect(screen.getByText(m.format("DD"))).toBeInTheDocument();
        });

        it("exposes the full localized date as a tooltip", () => {
            const timestamp = Date.UTC(2024, 0, 5, 12, 0, 0);
            const { container } = render(<TransactionCard {...makeTransaction({ timestamp })} />);

            const el = container.querySelector(".timestamp");
            expect(el).toHaveAttribute("title", dayjs(timestamp).format("llll"));
        });
    });

    describe("amount formatting", () => {
        it.each([
            ["10.00", "10"],
            ["10.50", "10.5"],
            ["10.55", "10.55"],
            ["0.10", "0.1"],
            ["0.00", "0"],
            ["1234.5", "1234.5"],
        ])("formats %s as %s", (amount, expected) => {
            const { container } = render(<TransactionCard {...makeTransaction({ amount })} />);

            expect(container.querySelector(".quantity")).toHaveTextContent(expected);
        });
    });
});

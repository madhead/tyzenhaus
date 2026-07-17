import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import dayjs from "../../datetime";
import { Members } from "../members";
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

const members = new Members([
    { id: 42, firstName: "Alice", lastName: "", username: null },
    { id: 1, firstName: "Bob", lastName: "", username: null },
    { id: 2, firstName: "Carol", lastName: "", username: null },
]);

function renderCard(overrides: Partial<Transaction> = {}, membersOverride: Members = members) {
    return render(<TransactionCard transaction={makeTransaction(overrides)} members={membersOverride} />);
}

describe("TransactionCard", () => {
    it("renders the title", () => {
        renderCard({ title: "Groceries" });

        expect(screen.getByText("Groceries")).toBeInTheDocument();
    });

    it("renders the currency", () => {
        renderCard({ currency: "USD" });

        expect(screen.getByText("USD")).toBeInTheDocument();
    });

    describe("timestamp", () => {
        it("shows the abbreviated month and zero-padded day of the transaction", () => {
            const timestamp = Date.UTC(2024, 0, 5, 12, 0, 0);
            renderCard({ timestamp });

            const m = dayjs(timestamp);
            expect(screen.getByText(m.format("MMM"))).toBeInTheDocument();
            expect(screen.getByText(m.format("DD"))).toBeInTheDocument();
        });

        it("exposes the full date as a tooltip using a 24-hour time (never AM/PM)", () => {
            const timestamp = Date.UTC(2024, 0, 5, 15, 30, 0);
            const { container } = renderCard({ timestamp });

            const title = container.querySelector(".timestamp")?.getAttribute("title") ?? "";

            expect(title).not.toBe("");
            expect(title).not.toMatch(/[AP]M/i);
            expect(title).toMatch(/\d{1,2}:\d{2}/);
        });
    });

    describe("title tooltip", () => {
        function mockOverflow(scrollWidth: number, clientWidth: number) {
            const scroll = vi.spyOn(HTMLElement.prototype, "scrollWidth", "get").mockReturnValue(scrollWidth);
            const client = vi.spyOn(HTMLElement.prototype, "clientWidth", "get").mockReturnValue(clientWidth);

            return () => {
                scroll.mockRestore();
                client.mockRestore();
            };
        }

        it("attaches the tooltip and full title only when the title is truncated", () => {
            const restore = mockOverflow(200, 100);

            try {
                const { container } = renderCard({ title: "A rather long title that does not fit" });
                const title = container.querySelector(".title");

                expect(title).toHaveClass("tooltip");
                expect(title).toHaveAttribute("title", "A rather long title that does not fit");
            } finally {
                restore();
            }
        });

        it("does not attach a tooltip when the title fits", () => {
            const restore = mockOverflow(100, 100);

            try {
                const { container } = renderCard({ title: "Short" });
                const title = container.querySelector(".title");

                expect(title).not.toHaveClass("tooltip");
                expect(title).not.toHaveAttribute("title");
            } finally {
                restore();
            }
        });
    });

    describe("participants", () => {
        it("renders the payer name followed by the recipient names", () => {
            const { container } = renderCard({ payer: 42, recipients: [1, 2] });

            expect(container.querySelector(".participants")).toHaveTextContent("Alice → Bob, Carol");
        });

        it("falls back to #id for unknown members", () => {
            const { container } = renderCard({ payer: 7, recipients: [1, 8] });

            expect(container.querySelector(".participants")).toHaveTextContent("#7 → Bob, #8");
        });

        it("renders just the payer name when there are no recipients", () => {
            const { container } = renderCard({ payer: 42, recipients: [] });

            expect(container.querySelector(".participants")).toHaveTextContent("Alice");
            expect(container.querySelector(".participants")?.textContent).not.toContain("→");
        });
    });

    describe("amount formatting", () => {
        it.each([
            ["10.00", "10.00"],
            ["10.50", "10.50"],
            ["10.55", "10.55"],
            ["0.10", "0.10"],
            ["0.00", "0.00"],
            ["1234.5", "1,234.50"],
        ])("formats %s as %s for a two-fraction-digit currency (EUR, locale en)", (amount, expected) => {
            const { container } = renderCard({ amount, currency: "EUR" });

            expect(container.querySelector(".quantity")).toHaveTextContent(expected);
        });

        it("uses the currency's fraction digits (JPY → 0)", () => {
            const { container } = renderCard({ amount: "1234", currency: "JPY" });

            expect(container.querySelector(".quantity")).toHaveTextContent("1,234");
        });

        it("does not repeat the currency code inside the quantity", () => {
            const { container } = renderCard({ amount: "10.00", currency: "EUR" });

            expect(container.querySelector(".quantity")?.textContent).toBe("10.00");
        });

        it("falls back to a plain localized decimal for an invalid currency code", () => {
            const { container } = renderCard({ amount: "1234.5", currency: "COOKIES" });

            expect(container.querySelector(".quantity")).toHaveTextContent("1,234.5");
        });
    });
});

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

    describe("title (scrollable when long)", () => {
        // The scroll geometry that decides truncation and which arrows show can't be computed by jsdom; fake it.
        function mockMetrics(scrollWidth: number, clientWidth: number, scrollLeft: number) {
            const sw = vi.spyOn(HTMLElement.prototype, "scrollWidth", "get").mockReturnValue(scrollWidth);
            const cw = vi.spyOn(HTMLElement.prototype, "clientWidth", "get").mockReturnValue(clientWidth);
            const sl = vi.spyOn(HTMLElement.prototype, "scrollLeft", "get").mockReturnValue(scrollLeft);

            return () => {
                sw.mockRestore();
                cw.mockRestore();
                sl.mockRestore();
            };
        }

        it("stays plain text with no tooltip or arrows when it fits", () => {
            const restore = mockMetrics(100, 100, 0);

            try {
                const { container } = renderCard({ title: "Short" });

                expect(container.querySelector(".title")).not.toHaveClass("scrollable");
                expect(container.querySelector(".scroller")).not.toHaveAttribute("title");
                expect(container.querySelector(".arrow")).toBeNull();
            } finally {
                restore();
            }
        });

        it("becomes scrollable with the full title and only the right arrow at the start", () => {
            const restore = mockMetrics(300, 100, 0);

            try {
                const { container } = renderCard({ title: "A very long grocery run title" });

                expect(container.querySelector(".title")).toHaveClass("scrollable");
                expect(container.querySelector(".scroller")).toHaveAttribute("title", "A very long grocery run title");
                expect(container.querySelector(".arrow.right")).not.toBeNull();
                expect(container.querySelector(".arrow.left")).toBeNull();
            } finally {
                restore();
            }
        });

        it("shows both arrows while scrolled in the middle", () => {
            const restore = mockMetrics(300, 100, 100);

            try {
                const { container } = renderCard({ title: "Long title" });

                expect(container.querySelector(".arrow.left")).not.toBeNull();
                expect(container.querySelector(".arrow.right")).not.toBeNull();
            } finally {
                restore();
            }
        });

        it("shows only the left arrow at the end", () => {
            const restore = mockMetrics(300, 100, 200);

            try {
                const { container } = renderCard({ title: "Long title" });

                expect(container.querySelector(".arrow.left")).not.toBeNull();
                expect(container.querySelector(".arrow.right")).toBeNull();
            } finally {
                restore();
            }
        });
    });

    describe("timestamp", () => {
        it("shows the abbreviated month and zero-padded day of the transaction", () => {
            const timestamp = Date.UTC(2024, 0, 5, 12, 0, 0);
            renderCard({ timestamp });

            const m = dayjs(timestamp);
            expect(screen.getByText(m.format("MMM"))).toBeInTheDocument();
            expect(screen.getByText(m.format("DD"))).toBeInTheDocument();
        });

        it("exposes the full date as an ISO-8601 tooltip (24-hour, never AM/PM)", () => {
            const timestamp = Date.UTC(2024, 0, 5, 15, 30, 0);
            const { container } = renderCard({ timestamp });

            const title = container.querySelector(".timestamp")?.getAttribute("title") ?? "";

            // Fixed `YYYY-MM-DD HH:mm` shape, independent of the runner's timezone.
            expect(title).toMatch(/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/);
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

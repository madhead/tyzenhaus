import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import ExpenseApp from "./ExpenseApp";

describe("ExpenseApp", () => {
    it("renders the heading", () => {
        render(<ExpenseApp />);

        expect(screen.getByRole("heading", { name: "Expense" })).toBeInTheDocument();
    });
});

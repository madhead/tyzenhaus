import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";
import Error from "./Error";

describe("Error", () => {
    it("renders the error message", () => {
        render(<Error error="Something went wrong" />);

        expect(screen.getByText("Something went wrong")).toBeInTheDocument();
    });

    it("renders inside the #error container", () => {
        const { container } = render(<Error error="Boom" />);

        expect(container.querySelector("#error")).toHaveTextContent("Boom");
    });
});

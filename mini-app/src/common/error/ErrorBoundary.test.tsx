import { render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import ErrorBoundary from "./ErrorBoundary";

vi.mock("react-i18next", () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

function Bomb(): never {
    throw new Error("boom");
}

describe("ErrorBoundary", () => {
    beforeEach(() => {
        vi.spyOn(console, "error").mockImplementation(() => undefined);
    });

    afterEach(() => {
        vi.restoreAllMocks();
    });

    it("renders its children when nothing fails", () => {
        render(
            <ErrorBoundary>
                <div>fine</div>
            </ErrorBoundary>,
        );

        expect(screen.getByText("fine")).toBeInTheDocument();
    });

    it("renders an error instead of the children that failed", () => {
        render(
            <ErrorBoundary>
                <Bomb />
            </ErrorBoundary>,
        );

        expect(screen.getByRole("alert")).toHaveTextContent("errors.generic");
    });
});

import { render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import Loading from "./Loading";

vi.mock("react-i18next", () => ({
    useTranslation: () => ({ t: (key: string) => key }),
}));

describe("Loading", () => {
    it("announces the loading state to assistive tech via role=status", () => {
        render(<Loading />);

        expect(screen.getByRole("status")).toHaveTextContent("loading");
    });
});

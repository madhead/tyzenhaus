import "@testing-library/jest-dom/vitest";

import { cleanup } from "@testing-library/react";
import { afterEach } from "vitest";

globalThis.ResizeObserver = class {
    observe() {}

    unobserve() {}

    disconnect() {}
} as unknown as typeof ResizeObserver;

afterEach(() => {
    cleanup();
});

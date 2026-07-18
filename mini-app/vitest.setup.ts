import "@testing-library/jest-dom/vitest";

import { cleanup } from "@testing-library/react";
import { afterEach } from "vitest";

// jsdom has no layout engine, so `ResizeObserver` is undefined; stub it as a no-op for components that observe.
// Assigned directly (not via `vi.stubGlobal`) so a test's `vi.unstubAllGlobals()` can't remove it.
globalThis.ResizeObserver = class {
    observe() {}
    unobserve() {}
    disconnect() {}
} as unknown as typeof ResizeObserver;

afterEach(() => {
    cleanup();
});

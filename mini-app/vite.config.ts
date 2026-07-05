import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import { viteStaticCopy } from "vite-plugin-static-copy";

export default defineConfig({
    base: "/app/",
    plugins: [
        react(),
        viteStaticCopy({
            targets: [
                { src: "src/i18n/*.json", dest: "i18n", rename: { stripBase: true } },
                { src: "../policies/build/*.{html,css}", dest: ".", rename: { stripBase: true } },
            ],
        }),
    ],
    build: {
        outDir: "build",
        emptyOutDir: true,
        sourcemap: true,
        rollupOptions: {
            input: {
                history: "history.html",
                expense: "expense.html",
            },
        },
    },
    server: {
        host: true,
        allowedHosts: true,
    },
});

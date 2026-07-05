import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import react from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";
import jsxA11y from "eslint-plugin-jsx-a11y";
import prettier from "eslint-config-prettier";
import { defineConfig, globalIgnores } from "eslint/config";

export default defineConfig(
    globalIgnores(["build/**", ".yarn/**", ".pnp.cjs", ".pnp.loader.mjs"]),

    js.configs.recommended,

    {
        files: ["src/**/*.{ts,tsx}"],
        extends: [
            tseslint.configs.recommendedTypeChecked,
            react.configs.flat.recommended,
            react.configs.flat["jsx-runtime"],
            jsxA11y.flatConfigs.recommended,
        ],
        languageOptions: {
            parserOptions: {
                projectService: true,
                tsconfigRootDir: import.meta.dirname,
            },
            globals: globals.browser,
        },
        settings: {
            react: { version: "detect" },
        },
        plugins: {
            "react-hooks": reactHooks,
        },
        rules: {
            "react-hooks/rules-of-hooks": "error",
            "react-hooks/exhaustive-deps": "warn",
        },
    },

    {
        files: ["webpack/**/*.ts"],
        extends: [tseslint.configs.recommended],
        languageOptions: {
            globals: globals.node,
        },
    },

    prettier,
);

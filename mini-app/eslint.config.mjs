import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import eslintReact from "@eslint-react/eslint-plugin";
import reactHooks from "eslint-plugin-react-hooks";
import jsxA11yX from "eslint-plugin-jsx-a11y-x";
import prettier from "eslint-config-prettier";
import { defineConfig, globalIgnores } from "eslint/config";

export default defineConfig(
    globalIgnores(["build/**", ".yarn/**", "node_modules/**"]),

    js.configs.recommended,

    {
        files: ["src/**/*.{ts,tsx}"],
        extends: [
            tseslint.configs.recommendedTypeChecked,
            eslintReact.configs["recommended-typescript"],
            jsxA11yX.configs.recommended,
        ],
        languageOptions: {
            parserOptions: {
                projectService: true,
                tsconfigRootDir: import.meta.dirname,
            },
            globals: globals.browser,
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
        files: ["vite.config.ts", "vitest.config.ts"],
        extends: [tseslint.configs.recommended],
        languageOptions: {
            globals: globals.node,
        },
    },

    {
        files: ["vitest.setup.ts"],
        extends: [tseslint.configs.recommended],
        languageOptions: {
            globals: globals.browser,
        },
    },

    prettier,
);

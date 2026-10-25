import React from "react";
import ReactDOM from "react-dom/client";
import "./expense.less";
import ErrorBoundary from "../common/error/ErrorBoundary";
import ExpenseApp from "./ExpenseApp";

const root = ReactDOM.createRoot(document.getElementById("root") as HTMLElement);

root.render(
    <React.StrictMode>
        <ErrorBoundary>
            <ExpenseApp />
        </ErrorBoundary>
    </React.StrictMode>,
);

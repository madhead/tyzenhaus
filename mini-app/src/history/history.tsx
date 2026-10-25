import React from "react";
import ReactDOM from "react-dom/client";

import "../i18n/i18n";

import "./history.less";

import AppWrapper from "../common/AppWrapper";
import ErrorBoundary from "../common/error/ErrorBoundary";
import { initTelegram } from "../common/telegram";
import HistoryApp from "./HistoryApp";

initTelegram();

const root = ReactDOM.createRoot(document.getElementById("root") as HTMLElement);

root.render(
    <React.StrictMode>
        <ErrorBoundary>
            <AppWrapper>
                <HistoryApp />
            </AppWrapper>
        </ErrorBoundary>
    </React.StrictMode>,
);

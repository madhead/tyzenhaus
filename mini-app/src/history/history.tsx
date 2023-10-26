import React from "react";
import ReactDOM from "react-dom/client";

import "@twa-dev/sdk";

import "../i18n/i18n";

import "./history.less";

import AppWrapper from "../common/AppWrapper";
import HistoryApp from "./HistoryApp";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

root.render(
  <React.StrictMode>
    <AppWrapper>
      <HistoryApp />
    </AppWrapper>
  </React.StrictMode>
);

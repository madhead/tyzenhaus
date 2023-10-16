import React from "react";
import ReactDOM from "react-dom/client";
import "./history.less";
import HistoryApp from "./HistoryApp";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

root.render(
  <React.StrictMode>
    <HistoryApp />
  </React.StrictMode>
);

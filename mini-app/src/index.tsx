import React from "react";
import ReactDOM from "react-dom/client";
import "./index.less";
import MiniApp from "./MiniApp";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);

root.render(
  <React.StrictMode>
    <MiniApp />
  </React.StrictMode>
);

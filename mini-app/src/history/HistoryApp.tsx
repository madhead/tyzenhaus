import "./HistoryApp.less";
import WebApp from "@twa-dev/sdk";

function HistoryApp() {
  console.log(WebApp.initData);
  console.log(WebApp.initDataUnsafe);
  return <h1>History</h1>;
}

export default HistoryApp;

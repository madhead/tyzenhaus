import { useEffect, useState } from "react";
import "./HistoryApp.less";
import WebApp from "@twa-dev/sdk";

function HistoryApp() {
  console.log(WebApp.initData);
  console.log(WebApp.initDataUnsafe);

  const [data, setData] = useState(null);

  useEffect(() => {
    async function fetchData() {
      const data = await fetch("/app/api/test");
      const json = await data.json();
      setData(json);
    }

    fetchData();
  }, []);

  return (
    <div>
      <h1>History</h1>
      Init Data: {WebApp.initData}
    </div>
  );
}

export default HistoryApp;

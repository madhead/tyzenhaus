import WebApp from "@twa-dev/sdk";
import { ReactNode, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import Error from "./error/Error";

type AppWrapperProps = {
  children: ReactNode;
};

export default function AppWrapper(props: AppWrapperProps) {
  const { platform } = WebApp;
  const { t } = useTranslation();

  if (platform === "unknown") {
    return <Error error={t("errors.outsideOfTelegram")} />;
  } else {
    return AuthWrapper(props);
  }
}

type AuthWrapperProps = AppWrapperProps;

function AuthWrapper({ children }: AuthWrapperProps) {
  const [ok, setOk] = useState<boolean | null>(null);
  const [expired, setExpired] = useState<boolean>(false);

  useEffect(() => {
    async function authenticate() {
      let response = await fetch("/app/api/auth/validation", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${WebApp.initDataUnsafe.start_param}`,
        },
        body: WebApp.initData,
      });

      setOk(response.ok);
      if (!response.ok) {
        setExpired(response.headers.has("X-Token-Expired"));
      }
    }

    authenticate();
  }, [
    WebApp,
    WebApp.initData,
    WebApp.initDataUnsafe,
    WebApp.initDataUnsafe.start_param,
  ]);

  const { t } = useTranslation();

  if (ok === null) {
    return null;
  } else if (ok === false) {
    if (expired) {
      return <Error error={t("errors.expired")} />;
    } else {
      return <Error error={t("errors.unauthorized")} />;
    }
  } else {
    return children;
  }
}

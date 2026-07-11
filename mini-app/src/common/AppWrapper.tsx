import WebApp from "@twa-dev/sdk";
import { ReactNode, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { authHeaders } from "./api";
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
        return <AuthWrapper {...props} />;
    }
}

type AuthWrapperProps = AppWrapperProps;

function AuthWrapper({ children }: AuthWrapperProps) {
    const [ok, setOk] = useState<boolean | null>(null);
    const [reason, setReason] = useState<string | null>(null);

    useEffect(() => {
        async function authenticate() {
            const response = await fetch("/app/api/auth/validation", {
                method: "POST",
                headers: authHeaders(),
            });

            setOk(response.ok);
            if (!response.ok) {
                setReason(response.headers.get("X-Auth-Error"));
            }
        }

        void authenticate();
    }, []);

    const { t } = useTranslation();

    if (ok === null) {
        return null;
    } else if (!ok) {
        return <Error error={t(errorKey(reason))} />;
    } else {
        return children;
    }
}

function errorKey(reason: string | null): string {
    switch (reason) {
        case "token_expired":
        case "invalid_token":
            return "errors.expired";
        case "not_a_participant":
            return "errors.notParticipant";
        case "invalid_init_data":
            return "errors.outsideOfTelegram";
        default:
            return "errors.unauthorized";
    }
}

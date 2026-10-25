import "./Loading.less";

import { useTranslation } from "react-i18next";

function Loading() {
    const { t } = useTranslation();

    return (
        <div className="loading" role="status">
            <span className="spinner" aria-hidden="true" />
            <span className="message">{t("loading")}</span>
        </div>
    );
}

export default Loading;

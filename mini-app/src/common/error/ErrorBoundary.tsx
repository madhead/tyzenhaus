import { Component, ErrorInfo, ReactNode } from "react";
import { useTranslation } from "react-i18next";
import Error from "./Error";

type ErrorBoundaryProps = {
    children: ReactNode;
};

type ErrorBoundaryState = {
    failed: boolean;
};

/**
 * Catches errors thrown while rendering the app, so a bug shows a message instead of a blank screen.
 */
export default class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
    state: ErrorBoundaryState = { failed: false };

    static getDerivedStateFromError(): ErrorBoundaryState {
        return { failed: true };
    }

    componentDidCatch(error: unknown, info: ErrorInfo) {
        console.error("Unhandled error in the Mini App", error, info.componentStack);
    }

    render() {
        return this.state.failed ? <Failure /> : this.props.children;
    }
}

function Failure() {
    const { t } = useTranslation();

    return <Error error={t("errors.generic")} />;
}

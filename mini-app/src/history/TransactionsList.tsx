import "./TransactionsList.less";

import { useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import Error from "../common/error/Error";
import Loading from "../common/loading/Loading";
import { Members } from "../common/members";
import TransactionCard from "../common/transaction/Transaction";
import { TransactionsSearchParams } from "../common/api";
import { useTransactions } from "./useTransactions";

type TransactionsListProps = {
    params: TransactionsSearchParams;
    filtered: boolean;
    members: Members;
};

/**
 * The transactions matching the [params], loaded page by page. Mount it anew (change its `key`) to search for
 * something else.
 */
function TransactionsList({ params, filtered, members }: TransactionsListProps) {
    const { t } = useTranslation();
    const { transactions, nextCursor, loading, failed, loadMore } = useTransactions(params);

    const empty = transactions.length === 0;

    if (empty && loading) {
        return <Loading />;
    }

    if (empty && failed) {
        return (
            <Error error={t("errors.loadFailed")}>
                <RetryButton onClick={loadMore} />
            </Error>
        );
    }

    if (empty) {
        return (
            <main aria-label="Transaction history">
                <p className="empty">{t(filtered ? "history.noMatches" : "history.empty")}</p>
            </main>
        );
    }

    return (
        <main aria-label="Transaction history">
            {/* eslint-disable-next-line jsx-a11y-x/no-redundant-roles */}
            <ul className="history" role="list">
                {transactions.map((transaction) => (
                    <li key={transaction.id}>
                        <TransactionCard transaction={transaction} members={members} />
                    </li>
                ))}
            </ul>
            {loading && <Loading />}
            {failed && (
                <div className="failure" role="alert">
                    {t("errors.loadFailed")}
                    <RetryButton onClick={loadMore} />
                </div>
            )}
            {nextCursor && !loading && !failed && <Sentinel key={nextCursor} onVisible={loadMore} />}
        </main>
    );
}

function RetryButton({ onClick }: { onClick: () => void }) {
    const { t } = useTranslation();

    return (
        <button type="button" className="retry" onClick={onClick}>
            {t("retry")}
        </button>
    );
}

/**
 * An invisible marker at the end of the list. Calls [onVisible] as soon as it scrolls into view (or right away, if the
 * loaded page didn't fill the screen).
 */
function Sentinel({ onVisible }: { onVisible: () => void }) {
    const ref = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const element = ref.current;

        if (!element) {
            return;
        }

        const observer = new IntersectionObserver((entries) => {
            if (entries.some((entry) => entry.isIntersecting)) {
                onVisible();
            }
        });

        observer.observe(element);

        return () => {
            observer.disconnect();
        };
    }, [onVisible]);

    return <div ref={ref} className="sentinel" aria-hidden="true" />;
}

export default TransactionsList;

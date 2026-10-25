import "./FilterBar.less";

import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { getCurrencies } from "../common/api";
import { Members } from "../common/members";
import { Filters } from "./filters";

type FilterBarProps = {
    filters: Filters;
    members: Members;
    onChange: (filters: Filters) => void;
};

/**
 * One control per search field. There is no submit button: every edit is applied to the search (after a short pause).
 */
function FilterBar({ filters, members, onChange }: FilterBarProps) {
    const { t } = useTranslation();
    const [currencies, setCurrencies] = useState<string[]>([]);

    useEffect(() => {
        // Without the currencies there's just nothing to choose from.
        getCurrencies().then(setCurrencies, (error: unknown) => {
            console.error("Failed to load the group currencies", error);
        });
    }, []);

    function change(patch: Partial<Filters>) {
        onChange({ ...filters, ...patch });
    }

    function toggleParticipant(id: number, selected: boolean) {
        change({
            participants: selected ? [...filters.participants, id] : filters.participants.filter((p) => p !== id),
        });
    }

    return (
        <form
            className="filters"
            role="search"
            aria-label={t("filters.label")}
            onSubmit={(event) => {
                event.preventDefault();
            }}
        >
            <input
                type="search"
                name="title"
                className="title"
                placeholder={t("filters.title")}
                aria-label={t("filters.title")}
                value={filters.title}
                onChange={(event) => {
                    change({ title: event.target.value });
                }}
            />

            <fieldset className="participants">
                <legend>{t("filters.participants")}</legend>
                {members.entries().map(({ id, name }) => (
                    <label key={id}>
                        <input
                            type="checkbox"
                            checked={filters.participants.includes(id)}
                            onChange={(event) => {
                                toggleParticipant(id, event.target.checked);
                            }}
                        />
                        {name}
                    </label>
                ))}
            </fieldset>

            <label className="currency">
                {t("filters.currency")}
                <select
                    value={filters.currency}
                    onChange={(event) => {
                        // Amounts of different currencies aren't comparable, so they go away with the currency.
                        change(
                            event.target.value
                                ? { currency: event.target.value }
                                : { currency: "", amountMin: "", amountMax: "" },
                        );
                    }}
                >
                    <option value="">{t("filters.anyCurrency")}</option>
                    {currencies.map((currency) => (
                        <option key={currency} value={currency}>
                            {currency}
                        </option>
                    ))}
                </select>
            </label>

            <label className="amount">
                {t("filters.amountMin")}
                <input
                    type="number"
                    inputMode="decimal"
                    min="0"
                    step="any"
                    disabled={!filters.currency}
                    value={filters.amountMin}
                    onChange={(event) => {
                        change({ amountMin: event.target.value });
                    }}
                />
            </label>
            <label className="amount">
                {t("filters.amountMax")}
                <input
                    type="number"
                    inputMode="decimal"
                    min="0"
                    step="any"
                    disabled={!filters.currency}
                    value={filters.amountMax}
                    onChange={(event) => {
                        change({ amountMax: event.target.value });
                    }}
                />
            </label>

            <label className="date">
                {t("filters.dateFrom")}
                <input
                    type="date"
                    value={filters.dateFrom}
                    max={filters.dateTo || undefined}
                    onChange={(event) => {
                        change({ dateFrom: event.target.value });
                    }}
                />
            </label>
            <label className="date">
                {t("filters.dateTo")}
                <input
                    type="date"
                    value={filters.dateTo}
                    min={filters.dateFrom || undefined}
                    onChange={(event) => {
                        change({ dateTo: event.target.value });
                    }}
                />
            </label>
        </form>
    );
}

export default FilterBar;

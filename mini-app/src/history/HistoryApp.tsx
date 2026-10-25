import { useEffect, useState } from "react";
import { Members, membersCache } from "../common/members";
import FilterBar from "./FilterBar";
import { Filters, isFiltered, NO_FILTERS, SEARCH_DEBOUNCE_MS, toSearchParams } from "./filters";
import TransactionsList from "./TransactionsList";
import { useDebounced } from "./useDebounced";

function HistoryApp() {
    const [filters, setFilters] = useState<Filters>(NO_FILTERS);
    const [members, setMembers] = useState<Members>(() => new Members());
    const params = toSearchParams(useDebounced(filters, SEARCH_DEBOUNCE_MS));

    useEffect(() => {
        // Names are a nicety: if they can't be loaded, the cards fall back to the member IDs.
        membersCache.get().then(setMembers, (error: unknown) => {
            console.error("Failed to load the group members", error);
        });
    }, []);

    return (
        <>
            <FilterBar filters={filters} members={members} onChange={setFilters} />
            <TransactionsList
                key={JSON.stringify(params)}
                params={params}
                filtered={isFiltered(params)}
                members={members}
            />
        </>
    );
}

export default HistoryApp;

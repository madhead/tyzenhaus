import WebApp from "@twa-dev/sdk";
import { DependencyList, useEffect, useState } from "react";

export default function useData<T>(url: string, deps: DependencyList = []): T | null {
    const [data, setData] = useState<T | null>(null);

    useEffect(() => {
        let ignore = false;
        const fetchData = async () => {
            const response = await fetch(url, {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${WebApp.initDataUnsafe.start_param}`,
                },
            });
            const json = (await response.json()) as T;
            if (!ignore) setData(json);
        };

        fetchData();

        return () => {
            ignore = true;
        };
    }, [url, ...deps]);

    return data;
}

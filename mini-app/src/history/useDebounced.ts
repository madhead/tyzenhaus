import { useEffect, useState } from "react";

/**
 * Follows [value], but only settles on a new one after it stopped changing for [delay] milliseconds.
 */
export function useDebounced<T>(value: T, delay: number): T {
    const [debounced, setDebounced] = useState(value);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDebounced(value);
        }, delay);

        return () => {
            clearTimeout(timer);
        };
    }, [value, delay]);

    return debounced;
}

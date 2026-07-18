import WebApp from "@twa-dev/sdk";
import { Transaction } from "./transaction/Transaction";

/**
 * Headers sent with every Mini App API request:
 * - the bearer token (group binding and scope) issued by the bot and passed as the launch `start_param`.
 * - raw `initData` the backend validates to authenticate the Telegram user.
 */
export function authHeaders(): Record<string, string> {
    return {
        "Authorization": `Bearer ${WebApp.initDataUnsafe.start_param}`,
        "X-Telegram-Init-Data": WebApp.initData,
    };
}

export type GroupMember = {
    id: number;
    firstName: string;
    lastName: string;
    username: string | null;
};

export type GroupMembers = {
    id: number;
    members: GroupMember[];
};

export type TransactionsPage = {
    transactions: Transaction[];
    nextCursor: string | null;
};

export type TransactionsSearchParams = {
    title?: string;
    participants?: number[];
    amountMin?: string;
    amountMax?: string;
    currencies?: string[];
    dateFrom?: string;
    dateTo?: string;
    cursor?: string;
    limit?: number;
};

export async function getMembers(): Promise<GroupMembers> {
    const response = await fetch("/app/api/group/members", {
        method: "GET",
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error(`GET /app/api/group/members failed: ${response.status}`);
    }

    const body = (await response.json()) as { id: number; members?: GroupMember[] };

    return { id: body.id, members: body.members ?? [] };
}

export async function getCurrencies(): Promise<string[]> {
    const response = await fetch("/app/api/group/currencies", {
        method: "GET",
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error(`GET /app/api/group/currencies failed: ${response.status}`);
    }

    return (await response.json()) as string[];
}

export async function searchTransactions(params: TransactionsSearchParams = {}): Promise<TransactionsPage> {
    const query = new URLSearchParams();

    if (params.title) {
        query.append("title", params.title);
    }

    for (const participant of params.participants ?? []) {
        query.append("participant", String(participant));
    }

    if (params.amountMin) {
        query.append("amountMin", params.amountMin);
    }

    if (params.amountMax) {
        query.append("amountMax", params.amountMax);
    }

    for (const currency of params.currencies ?? []) {
        query.append("currency", currency);
    }

    if (params.dateFrom) {
        query.append("dateFrom", params.dateFrom);
    }

    if (params.dateTo) {
        query.append("dateTo", params.dateTo);
    }

    if (params.cursor) {
        query.append("cursor", params.cursor);
    }

    if (params.limit) {
        query.append("limit", String(params.limit));
    }

    const queryString = query.toString();
    const url = queryString ? `/app/api/group/transactions?${queryString}` : "/app/api/group/transactions";

    const response = await fetch(url, {
        method: "GET",
        headers: authHeaders(),
    });

    if (!response.ok) {
        throw new Error(`GET /app/api/group/transactions failed: ${response.status}`);
    }

    const body = (await response.json()) as { transactions: Transaction[]; nextCursor?: string | null };

    return { transactions: body.transactions, nextCursor: body.nextCursor ?? null };
}

import { getMembers, GroupMember } from "./api";

export class Members {
    private readonly members: Map<number, GroupMember>;

    constructor(members: GroupMember[] = []) {
        this.members = new Map(members.map((member) => [member.id, member]));
    }

    name(id: number): string {
        const member = this.members.get(id);

        if (member) {
            const fullName = [member.firstName, member.lastName]
                .map((part) => (part ?? "").trim())
                .filter((part) => part.length > 0)
                .join(" ");

            if (fullName.length > 0) {
                return fullName;
            }

            if (member.username && member.username.trim().length > 0) {
                return member.username;
            }
        }

        return `#${id}`;
    }
}

export class MembersCache {
    private members: Promise<Members> | null = null;

    get(): Promise<Members> {
        if (!this.members) {
            this.members = getMembers()
                .then(({ members }) => new Members(members))
                .catch((error) => {
                    this.members = null;
                    throw error;
                });
        }

        return this.members;
    }

    reset(): void {
        this.members = null;
    }
}

export const membersCache = new MembersCache();

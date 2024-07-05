import { ReactNode, createContext, useContext } from "react";
import useData from "../hooks/useData";

type Participant = {
  id: number;
  firstName: string;
  lastName: string;
  username: string | null;
};
type Participants = { members: Participant[] };

const ParticipantsContext = createContext<Participant[] | null>(null);

type ParticipantsProviderProps = {
  children: ReactNode;
};

export function ParticipantsProvider(props: ParticipantsProviderProps) {
  const participants =
    useData<Participants>("/app/api/group/participants", [])?.members ?? null;

  if (participants) {
    return (
      <ParticipantsContext.Provider value={participants}>
        {props.children}
      </ParticipantsContext.Provider>
    );
  } else {
    return null;
  }
}

export function useParticipants() {
  return useContext(ParticipantsContext);
}

/**
 * If the requested participant has a unique username, return it.
 *
 * Otherwise, return the participant's first name and last name.
 *
 * Finally, if even that in not unique, return first name, last name + user name.
 */
export function participantName(
  id: number,
  participants: Participant[]
): string {
  const participant = participants.find((p) => p.id === id);

  if (!participant) {
    throw new Error(`Participant with id ${id} not found`);
  }

  if (
    participants.filter((p) => p.firstName === participant.firstName).length ===
    1
  ) {
    return participant.firstName;
  } else if (
    participants.filter(
      (p) =>
        p.firstName === participant.firstName &&
        p.lastName === participant.lastName
    ).length === 1
  ) {
    return `${participant.firstName} ${participant.lastName}`;
  } else {
    if (participant.username) {
      return `${participant.firstName} ${participant.lastName} (${participant.username})`;
    } else {
      return `${participant.firstName} ${participant.lastName}`;
    }
  }
}

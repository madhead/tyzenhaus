import { ReactNode, createContext, useContext } from "react";
import useData from "../hooks/useData";

type Currency = string;

const CurrenciesContext = createContext<Currency[] | null>(null);

type CurrenciesProviderProps = {
  children: ReactNode;
};

export function CurrenciesProvider(props: CurrenciesProviderProps) {
  const currencies = useData<Currency[]>("/app/api/group/currencies", []);

  if (currencies) {
    return (
      <CurrenciesContext.Provider value={currencies}>
        {props.children}
      </CurrenciesContext.Provider>
    );
  } else {
    return null;
  }
}

export function useCurrencies() {
  return useContext(CurrenciesContext);
}

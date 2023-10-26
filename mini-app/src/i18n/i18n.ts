import WebApp from "@twa-dev/sdk";
import i18next from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import HttpBackend from "i18next-http-backend";
import moment from "moment";
import { initReactI18next } from "react-i18next";

const locale = WebApp.initDataUnsafe.user?.language_code || "en";

document.documentElement.lang = locale;
moment.locale(locale);

const httpBackend = new HttpBackend(null, {
    loadPath: "/app/i18n/{{lng}}.json",
});

const languageDetector = new LanguageDetector(null, {
    order: ["htmlTag"],
});

i18next.use(httpBackend).use(languageDetector).use(initReactI18next).init({
    fallbackLng: "en",
});

export default i18next;

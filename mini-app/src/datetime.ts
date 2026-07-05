import dayjs from "dayjs";
import "dayjs/locale/it";
import "dayjs/locale/pt";
import "dayjs/locale/ru";
import localizedFormat from "dayjs/plugin/localizedFormat"; // enables the "llll" token

dayjs.extend(localizedFormat);

export default dayjs; // "en" is built into the core, no import needed

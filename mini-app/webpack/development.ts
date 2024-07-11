import merge from "webpack-merge";
import common from "./common";

export default merge(common, {
    mode: "development",
    devtool: "eval-source-map",
    output: {
        filename: "[name].js",
        chunkFilename: "[id].chunk.js",
    },
    devServer: {
        allowedHosts: "all",
    },
});

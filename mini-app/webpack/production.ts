import merge from "webpack-merge";
import common from "./common";

export default merge(common, {
    mode: "production",
    devtool: "source-map",
    output: {
        filename: "[name].[contenthash].js",
        chunkFilename: "[id].[contenthash].chunk.js",
    },
});

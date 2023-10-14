import path from "path";
import webpack from "webpack";
import "webpack-dev-server";
import HtmlWebpackPlugin from "html-webpack-plugin";

const root = path.resolve(__dirname, "..");
const src = path.resolve(root, "src");

const configuration: webpack.Configuration = {
    entry: {
        app: [path.resolve(src, "index.tsx")],
    },
    output: {
        path: path.resolve(root, "build"),
    },
    optimization: {
        splitChunks: {
            chunks: "all",
        },
        realContentHash: true,
    },
    resolve: {
        extensions: [".ts", ".tsx", ".js", ".jsx"],
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: [
                    {
                        loader: "ts-loader",
                    },
                ],
            },
            {
                test: /\.less$/,
                use: [
                    {
                        loader: "style-loader",
                    },
                    {
                        loader: "css-loader",
                    },
                    {
                        loader: "less-loader",
                    },
                ],
            },
        ],
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: path.resolve(src, "index.html"),
            hash: true,
        }),
    ],
};

export default configuration;

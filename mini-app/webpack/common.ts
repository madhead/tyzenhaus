import path from "path";
import webpack from "webpack";
import "webpack-dev-server";
import HtmlWebpackPlugin from "html-webpack-plugin";
import CopyPlugin from "copy-webpack-plugin";

const root = path.resolve(__dirname, "..");
const src = path.resolve(root, "src");
const build = path.resolve(root, "build");

const configuration: webpack.Configuration = {
    entry: {
        history: [path.resolve(src, "history/history.tsx")],
        expense: [path.resolve(src, "expense/expense.tsx")],
    },
    output: {
        path: build,
        publicPath: "/app",
    },
    optimization: {
        splitChunks: {
            cacheGroups: {
                default: false,
                vendor: {
                    test: /[\\/]node_modules[\\/](react|react-dom)[\\/]/,
                    name: "react",
                    chunks: "all",
                },
            },
        },
        realContentHash: true,
    },
    resolve: {
        extensions: [".ts", ".tsx", ".js", ".jsx"],
    },
    module: {
        rules: [
            {
                test: /\.m?js/,
                resolve: {
                    fullySpecified: false,
                },
            },
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
            {
                test: /\.css$/,
                use: [
                    {
                        loader: "style-loader",
                    },
                    {
                        loader: "css-loader",
                    },
                ],
            },
        ],
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: path.resolve(src, "index.html"),
            hash: true,
            filename: "history.html",
            chunks: ["history"],
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(src, "index.html"),
            hash: true,
            filename: "expense.html",
            chunks: ["expense"],
        }),
        new CopyPlugin({
            patterns: [
                {
                    from: path.join(src, "/i18n"),
                    to: path.join(build, "/i18n"),
                },
            ],
        }),
    ],
};

export default configuration;

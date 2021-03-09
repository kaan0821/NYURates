const path = require("path");
const webpack = require("webpack");

module.exports = {
    entry: "./src/js/bundle.js",
    mode: "development",
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /(node_modules|bower_components)/,
                loader: "babel-loader",
                options: { presets: ["@babel/env"] }
            },
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"]
            }
        ]
    },
    resolve: { extensions: ["*", ".js", ".jsx"] },
    output: {
        path: path.resolve(__dirname, "/public"),
        publicPath: "/public/",
        filename: "[name].bundle.js",
        chunkFilename: "[name].bundle.js"
    },
    devServer: {
        open:true,
        compress: true,
        contentBase: path.join(__dirname, "/public"),
        port: 8080,
        publicPath: "/public/",
        hotOnly: true,
        historyApiFallback: true
    },
    plugins: [new webpack.HotModuleReplacementPlugin()],
    performance: {
        hints: false
    }
};
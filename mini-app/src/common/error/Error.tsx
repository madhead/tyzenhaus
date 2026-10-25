import "./Error.less";

import { ReactNode } from "react";

type ErrorProps = {
    error: string;
    children?: ReactNode;
};

function Error({ error, children }: ErrorProps) {
    return (
        <div id="error" role="alert">
            {error}
            {children}
        </div>
    );
}

export default Error;

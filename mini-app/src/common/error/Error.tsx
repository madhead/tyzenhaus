import "./Error.less";

type ErrorProps = {
    error: string;
};

function Error({ error }: ErrorProps) {
    return (
        <div id="error" role="alert">
            {error}
        </div>
    );
}

export default Error;

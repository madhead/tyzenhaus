import "./Error.less";

type ErrorProps = {
  error: string;
};

function Error({ error }: ErrorProps) {
  return <div id="error">{error}</div>;
}

export default Error;

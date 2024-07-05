import { useEffect, useRef, useState } from "react";
import { default as RFM } from "react-fast-marquee";
import "./Marquee.less";

type MarqueeProps = { children: React.ReactNode };

export default function Marquee(props: MarqueeProps) {
  const childrenContainerRef = useRef<HTMLDivElement>(null);
  const [marquee, setMarquee] = useState(false);

  useEffect(() => {
    if (!childrenContainerRef.current) {
      return;
    }

    const element = childrenContainerRef.current;

    if (
      element.offsetWidth < element.scrollWidth ||
      element.offsetHeight < element.scrollHeight
    ) {
      setMarquee(true);
    }
  }, [childrenContainerRef]);

  return (
    <div className="marquee" ref={childrenContainerRef}>
      {marquee && (
        <RFM delay={3}>
          {props.children}
          <span className="spacer" />
        </RFM>
      )}
      {!marquee && props.children}
    </div>
  );
}

FROM openjdk:13-alpine

RUN addgroup -S tyzenhaus && \
    adduser -S -G tyzenhaus tyzenhaus
USER tyzenhaus

ADD --chown=tyzenhaus:tyzenhaus ./build/install/tyzenhaus/ /opt/tyzenhaus/

LABEL maintainer="madhead <siarhei.krukau@gmaik.com>"

CMD /opt/tyzenhaus/bin/tyzenhaus

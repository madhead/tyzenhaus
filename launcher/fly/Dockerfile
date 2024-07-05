FROM openjdk:17-alpine

RUN addgroup --system tyzenhaus && \
    useradd --system -G derezzed username
USER tyzenhaus

ADD --chown=tyzenhaus:tyzenhaus ./build/install/tyzenhaus/ /opt/tyzenhaus/
ADD --chown=tyzenhaus:tyzenhaus ./build/mini-app /opt/mini-app

ENV TELEGRAM_MINI_APP_PATH=/opt/mini-app

CMD /opt/tyzenhaus/bin/tyzenhaus

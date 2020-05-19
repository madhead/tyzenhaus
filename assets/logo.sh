#!/usr/bin/env bash

set -x

SRC="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
DEST="$(dirname "$SRC")"

docker run \
    --rm \
    -it \
    -v $SRC:/src \
    -v $DEST:/dest \
    --user=$(id -u):$(id -g) \
    madhead/imagemagick \
    convert -density 300 -resize 512x512 /src/logo.svg /dest/logo.png

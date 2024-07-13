#!/usr/bin/env bash

pushd ../..
./gradlew :policies:clean :policies:privacy
popd

pushd ../../mini-app
yarn clean && yarn build
popd

pushd ../..
./gradlew clean install
popd

cp -r ../../mini-app/build ./build/mini-app

fly deploy

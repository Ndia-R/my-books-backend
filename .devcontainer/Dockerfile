FROM openjdk:17-jdk-slim

WORKDIR /my-books-backend

ARG PKG="git curl unzip zip sudo"

RUN apt-get update \
    && apt-get install -y ${PKG} \
    && rm -rf /var/lib/apt/lists/*

ARG GRADLE_VERSION=8.10

RUN curl -s "https://get.sdkman.io" | bash \
    && bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk install gradle $GRADLE_VERSION"

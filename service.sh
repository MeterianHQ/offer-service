#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

DOCKER_EXPOSE_PORT="8080:8080"

DOCKER_IMAGE_LABEL="ovoenergy/offer-service"

# build local jar file
dev_build() {
    ./gradlew clean build
}

# runs application locally
dev_run() {
    java -jar build/libs/offer-service-0.1.0.jar
}

# runs application locally in debug mode
dev_run_debug() {
    java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar build/libs/offer-service-0.1.0.jar
}

# builds docker image
docker_build() {
    ./gradlew clean build buildDocker
}

# runs docker image
docker_run() {
    docker run -d -p $DOCKER_EXPOSE_PORT -t $DOCKER_IMAGE_LABEL
 }

case $1 in
    dev_build)
        dev_build
        ;;
    dev_run)
        dev_run
        ;;
    docker_build)
        docker_build
        ;;
    docker_run)
        docker_run
        ;;
    dev_run_debug)
        dev_run_debug
        ;;
    *)
        echo "Supported actions: $0 {dev_build|dev_run|docker_build|docker_run}"
esac
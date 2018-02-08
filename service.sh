#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

DOCKER_EXPOSE_PORT="8080:8080"

DOCKER_IMAGE_LABEL="offer-service"

# build local jar file
dev_build() {
    ./gradlew clean build
}

# create offers_db
create_db() {
    cd db-scripts
    gradle liquibaseCreate liquibaseUpdate -DuserName=postgres -Dpassword=postgres -Durl=jdbc:postgresql://localhost/offers_engine
    cd ../
}

# re-create offers_db
re_create_db() {
    cd db-scripts
    gradle liquibaseDropAll liquibaseCreate liquibaseUpdate -DuserName=postgres -Dpassword=postgres -Durl=jdbc:postgresql://localhost/offers_engine
    cd ../
}

# update offers_db
update_db() {
    cd db-scripts
    gradle liquibaseUpdate -DuserName=postgres -Dpassword=postgres -Durl=jdbc:postgresql://localhost/offers_engine
    cd ../
}

# runs application locally
dev_run() {
    java -jar build/libs/offer-service-0.1.0.jar --spring.datasource.url=jdbc:postgresql://localhost/offers_engine --spring.datasource.username=postgres --spring.datasource.password=postgres

}

# runs application locally in debug mode
dev_run_debug() {
    java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n -jar build/libs/offer-service-0.1.0.jar --spring.datasource.url=jdbc:postgresql://localhost/offers_engine --spring.datasource.username=postgres --spring.datasource.password=postgres
}

# builds docker image
docker_build() {
    ./gradlew clean build buildDocker
}

# runs docker image
docker_run() {
    docker run -d -p $DOCKER_EXPOSE_PORT -t $DOCKER_IMAGE_LABEL --ignore-pull-failures
 }

case $1 in
    dev_build)
        dev_build
        ;;
    create_db)
        create_db
        ;;
    re_create_db)
        re_create_db
        ;;
    update_db)
        create_db
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
        echo "Supported actions: $0 {dev_build|dev_run|docker_build|docker_run|create_db|re_create_db|update_db}"
esac
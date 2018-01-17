FROM java:8
VOLUME /tmp
ADD offer-service-0.1.0.jar offer-service-0.1.0.jar
RUN bash -c 'touch /offer-service-0.1.0.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/offer-service-0.1.0.jar"]
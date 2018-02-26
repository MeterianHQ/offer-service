FROM java:8
VOLUME /tmp
ADD offer-service-0.1.0.jar offer-service-0.1.0.jar
RUN bash -c 'touch /offer-service-0.1.0.jar'
ENTRYPOINT ["sh", "-c", "java ${EXTRA_JAVA_VARS} -agentlib:jdwp=transport=dt_socket,address=50505,suspend=n,server=y -Djava.security.egd=file:/dev/./urandom -jar /offer-service-0.1.0.jar"]
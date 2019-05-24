#openjdk:8-jdk-alpine
FROM openjdk:8-jre-alpine

#VOLUME /tmp
ADD ["build/libs/gateway-service.jar", "gateway-service.jar"]

# touch the archive for timestamp
RUN sh -c 'touch /gateway-serivce.jar'

#docker run -e "JAVA_OPTS=-Xmx128m"
#ENV JAVA_OPTS=""

#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom", "-jar", "eureka-service.jar"]
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "gateway-service.jar"]

EXPOSE 8082


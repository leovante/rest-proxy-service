FROM openjdk:21
COPY target/rest-proxy-stub-1.0.jar /usr/local/service/
ENTRYPOINT ["java", "-jar", "/usr/local/service/rest-proxy-stub-1.0.jar"]
#dev2

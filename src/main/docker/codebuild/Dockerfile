#
# This is the "production" Dockerfile. It creates a very lean image with a JRE and the Einvite JAR, and
# not a whole lot else.
#
FROM openjdk:8-jdk-alpine as build
MAINTAINER Peter Keeler <peter@bonevm.com>
WORKDIR /opt/build
COPY . /opt/build/
RUN cd /opt/build \
&& apk update \
&& apk upgrade \
&& apk add --no-cache bash \
&& apk add libstdc++ \
&& ./gradlew --console=plain clean build -x buildDocker -x dependencyCheckAnalyze

FROM openjdk:8-jre-alpine as run
MAINTAINER Peter Keeler <peter@bonevm.com>
EXPOSE 8080
COPY --from=build /opt/build/build/libs/einvite-*.jar /opt/app/app.jar
CMD ["/usr/bin/java", "-jar","/opt/app/app.jar"]

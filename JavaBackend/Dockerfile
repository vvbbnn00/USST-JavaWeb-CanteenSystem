FROM tomcat:10
LABEL authors="vvbbnn00"

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY ./target/CanteenSystemJavaBackend-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

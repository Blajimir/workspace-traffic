FROM maven:3.6.0-jdk-8
RUN mkdir /workspace-traffic
WORKDIR ./..
COPY ./workspace-traffic-0.0.1-SNAPSHOT.jar /workspace-traffic
#RUN git clone https://github.com/Blajimir/workspace-traffic.git
WORKDIR workspace-traffic
#RUN git checkout -b developer
#RUN mvn dependency:copy-dependencies
#CMD git pull
#CMD mvn -DskipTests=true  package
#CMD java -jar ./target/workspace-traffic-0.0.1-SNAPSHOT.jar
CMD java -jar ./workspace-traffic-0.0.1-SNAPSHOT.jar
HEALTHCHECK --interval=5m --timeout=10s CMD curl -f http://localhost:8085/api/health || exit 1
EXPOSE 8085

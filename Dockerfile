FROM maven:3.5.4-jdk-8-alpine
RUN mkdir /workspace-traffic
WORKDIR /home/ec2-user
COPY ./workspace-traffic-0.0.1-SNAPSHOT.jar /workspace-traffic
CMD java -jar /workspace-traffic/workspace-traffic-0.0.1-SNAPSHOT.jar
EXPOSE 8085

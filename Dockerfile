FROM maven:3.6.0-jdk-8
RUN mkdir /workspace-traffic
WORKDIR /home/ec2-user
COPY ./workspace-traffic-0.0.1-SNAPSHOT.jar /workspace-traffic
CMD java -jar /workspace-traffic/workspace-traffic-0.0.1-SNAPSHOT.jar
EXPOSE 8085

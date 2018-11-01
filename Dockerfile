FROM maven:3.5.4-jdk-8-alpine
COPY /home/ec2-user/workspace-traffic-0.0.1-SNAPSHOT.jar /opt/workspace-traffic
CMD java -jar /opt/workspace-traffic/workspace-traffic-0.0.1-SNAPSHOT.jar
EXPOSE 8085

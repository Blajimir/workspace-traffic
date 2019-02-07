FROM maven:3.6.0-jdk-8
RUN mkdir /workspace-traffic
WORKDIR /home/ec2-user
COPY ./workspace-traffic-0.0.1-SNAPSHOT.jar /workspace-traffic
#RUN git clone https://github.com/Blajimir/workspace-traffic.git
#WORKDIR workspace-traffic
#RUN git checkout -b developer
#RUN mvn dependency:copy-dependencies
#CMD git pull
#CMD mvn -DskipTests=true  package
#CMD ls -l
#CMD java -jar target/workspace-traffic-0.0.1-SNAPSHOT.jar
CMD java -jar /workspace-traffic/workspace-traffic-0.0.1-SNAPSHOT.jar
EXPOSE 8085

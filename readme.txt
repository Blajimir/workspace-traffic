#help to install with docker
POSTGRES_DB
#Create DB for main service
$ docker run --name traffic-postgres -e POSTGRES_PASSWORD=traffic -e POSTGRES_DB=workspace-traffic  -d postgres:10.7-alpine 

#Create and run object-detection service for main service [optional]
$ docker build -t tf-od-rest-api -f Dockerfile .
$ docker run --name mytf -d tf-od-rest-api

#Create and run main worspace-traffic app
$ docker build -t workspace-traffic:v1 -f Dockerfile .
$ docker run --name wscp-traffic-service --link traffic-postgres:postgres --link mytf:rest-tf-od -p 8085:80 -d workspace-traffic:v1
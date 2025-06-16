mvn clean install
docker build . -t application:1.0.0
docker compose up -d
docker logs -f ims-application-1

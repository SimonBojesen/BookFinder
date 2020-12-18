From openjdk:15
copy ./target/bookfinder-0.0.1-SNAPSHOT.jar bookfinder-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","bookfinder-0.0.1-SNAPSHOT.jar"]
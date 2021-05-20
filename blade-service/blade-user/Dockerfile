FROM guanzhiguo/auth-service:release-1.0.0

MAINTAINER smallchill@163.com

RUN mkdir -p /blade/user

WORKDIR /blade/user

EXPOSE 8102

ADD ./target/blade-user.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=test"]

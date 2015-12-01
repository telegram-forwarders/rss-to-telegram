FROM jamespedwards42/docker-ubuntu-java-8
ADD . /app
WORKDIR /app
ENTRYPOINT ["java", "-jar", "out/artifacts/rss_to_telegram_jar/rss_to_telegram.jar"]
CMD ["--token=", "--chatId=", "--author=", "--interval=300", "--rssUrl=", "--name=ura_bot"]
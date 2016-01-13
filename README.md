# rss-to-telegram

### Requrements
- java8
- telegram bot (register here)

### Usage

```shell
java -jar out/artifacts/rss_to_telegram_jar/rss_to_telegram.jar --param=value
```

#### params:
- token - Required, Telegram token
- chatId - Required, Telegram chatId/room
- interval - Required, Pooling interval
- rssUrl - Required, Rss channel
- author - Author name, news will be filtered by this param (=contains). Could be null

# rss-to-telegram

### Requrements
- java8
- telegram bot (read here - https://core.telegram.org/bots)

### Usage

```shell
java -jar rss_to_telegram.jar --param=value
```

#### params:
- token:string - Required, Telegram token
- chatId:number - Required, Telegram chatId/room
- interval:number - Required, Pooling interval (default 300s)
- rssUrl:string - Required, Rss channel
- author:string - Author name, news will be filtered by this param (=contains). Could be null

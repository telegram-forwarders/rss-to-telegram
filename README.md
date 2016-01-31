# rss-to-telegram

### Requrements
- java8
- telegram bot (read here - https://core.telegram.org/bots)

### Usage

```shell
usage: java rss_to_telegram.jar [-c <arg>]
Options
   -c,--config <arg>     Path to config file
```

```javascript
{
  "baseTelegramToken": "TOKEN", // required
  "baseTelegramChatId": "CHAT_ID", // required
  "rssData": [
    {
      "telegramToken": "TOKEN", // could be null
      "telegramChatId": "CHAT_ID", // could be null
      "name": "NAME", // could be null
      "url": "http://rss.org", // required
      "interval": 300 // required
    }
  ]
}
```

If **telegramToken** and **telegramChatId** are null, will be use **baseTelegramToken** and **baseTelegramChatId** as default.

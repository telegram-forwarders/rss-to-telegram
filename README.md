# rss-to-telegram

### Requrements
- java8
- telegram bot (read here - https://core.telegram.org/bots)

### Usage

```shell
usage: java rss_to_telegram.jar [-c <arg>] [-i <arg>] [-t <arg>] [-u <arg>]
Options
   -c,--chatId <arg>       Telegram chat id. [required, integer]
   -i,--interval <arg>     Pooling interval. [optional(default=300), integer]
   -t,--token <arg>        Telegram bot token. [required, string]
   -u,--rssUrl <arg>       RSS/Author combination. Use :: as a delimiter.
                           Example: http://rss.com::john_doe or http://rss.com.
                           [required, string]
```

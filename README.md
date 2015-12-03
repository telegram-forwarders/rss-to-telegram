# rss-to-telegram

### Requrements
- java8
- telegram bot (register here)

### Usage

java -jar out/artifacts/rss_to_telegram_jar/rss_to_telegram.jar --token=<token> --chatId=<chatId> --author=<author> --interval=<interval> --rssUrl=<rssUrl> --name=<name>

#### params:
- token - Required, Telegram token
- chatId - Required, Telegram chatId/room
- interval - Required, Pooling interval
- rssUrl - Required, Rss channel
- author - Author name, news will be filtered by this param (=contains). Could be null

### Docker

#### Build image
sudo docker build -t <image_name>

#### Run container
sudo docker run -d --name=<container_name> <image_name> --token=<token> --chatId=<chatId> --author=<author> --interval=<interval> --rssUrl=<rssUrl> --name=<name>

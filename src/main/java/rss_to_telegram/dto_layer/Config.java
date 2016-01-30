package rss_to_telegram.dto_layer;

import java.util.ArrayList;

public class Config {
    public String baseTelegramToken;
    public String baseTelegramChatId;
    public ArrayList<ConfigRssItem> rssData;
}

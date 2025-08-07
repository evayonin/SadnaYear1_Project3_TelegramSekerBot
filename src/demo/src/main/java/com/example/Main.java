// 7/8/25 next time continue from showSekerData() and check the bot
package com.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new SekerBot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        SekerBot sekerBot = new SekerBot();
        // כדי לוודא שהחלון יווצר רק אחרי שהסקר יסתיים צריך לבדוק בלולאה שכל עוד חוזר
        // מאותה מתודה נאל, ז״א שהסקר לא הגיע לשלב האחרון, אז לא ייצור את החלון.
        while (sekerBot.getFinalDataMap() == null) { //
            if (sekerBot.getFinalDataMap() != null) {
                new ResultsWindow(sekerBot.getFinalDataMap());
                break;
            }
        }
        // כדי לא לעשות חיים קשים נכתוב את התוצאות כטקסט בסווינג ואולי כגרף עמודות ולא
        // עוגה.
        // לשאול את אביה במה היא השתמפה כדי להציג כגרף עוגה - אם יש מתודות מובנות
        // בסווינג שמאפשרות.
    }
}
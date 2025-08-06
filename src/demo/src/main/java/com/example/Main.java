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

        // לעשות שהחלון ייפעל רק ברגע שהסקר יסתיים ולא מהרגע שהתכנית מתחילה לרוץ.
        // כנראה שנכתוב את זה בתוך המחלקה של הבוט אחרי כל השלבים בonUpdateRecieved
        // ברגע שיסתיים הסקר לשמור את המידע ונשלח אותו מאותה המתודה בבוט למחלקת הפאנל.
        // נחבר את הפאאנל לחלון.
        // כתבתי כאן כדי לא לשכוח!
        // new ResultsWindow();

        // כדי לא לעשות חיים קשים נכתוב את התוצאות כטקסט בסווינג ואולי כגרף עמודות ולא
        // עוגה.
        // לשאול את אביה במה היא השתמפה כדי להציג כגרף עוגה - אם יש מתודות מובנות
        // בסווינג שמאפשרות.
    }
}
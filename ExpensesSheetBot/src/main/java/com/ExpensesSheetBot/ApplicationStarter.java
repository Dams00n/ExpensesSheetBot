package com.ExpensesSheetBot;

import com.ExpensesSheetBot.TelegramBotAPI.ExpensesSheetBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

public class ApplicationStarter {

    public static void main(String... args) throws IOException,
            GeneralSecurityException, TelegramApiException, URISyntaxException {


        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new ExpensesSheetBot());


    }
}

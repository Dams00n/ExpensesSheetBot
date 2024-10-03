package com.ExpensesSheetBot.TelegramBotAPI;

import com.ExpensesSheetBot.GoogleSheetsAPI.ApiSheet;
import com.ExpensesSheetBot.GoogleSheetsAPI.SheetApiConnectionService;
import com.ExpensesSheetBot.Service.CommonMethods;
import com.google.api.services.sheets.v4.Sheets;
import lombok.SneakyThrows;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensesSheetBot extends TelegramLongPollingBot {

    Sheets connectedSheet;
    List<List<Object>> categories;
    static String callback;

    public ExpensesSheetBot() throws URISyntaxException, IOException, GeneralSecurityException {
        super(new CommonMethods().getConstant("bot.Token"));
        this.connectedSheet = new SheetApiConnectionService().connect();
        try {
            this.categories = new ApiSheet().getCategories(this.connectedSheet);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text

        try {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (update.hasMessage() && update.getMessage().hasText()) {

                chooseCommandOrMessage(update, chatId, message);

            } else if (update.hasCallbackQuery()) {

                callback = update.getCallbackQuery().getData();
                long chatID = update.getCallbackQuery().getMessage().getChatId();
                workingWithRespondsOfButtons(chatID);
            }
        } catch (URISyntaxException | IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);

        }

    }

    private void workingWithRespondsOfButtons(long chatID) throws TelegramApiException, URISyntaxException, IOException {
        if (NumberUtils.isParsable(callback)) {
            execute(chooseExpense(chatID, callback));
        } else {
            //TODO Вспомнить, что я еще хотел добавить при работе с кнопками
        }
    }

    private void chooseCommandOrMessage(Update update, long chatId, String message) throws TelegramApiException, URISyntaxException, IOException {
        if(update.getMessage().getText().equals("/start")) {
            execute(sendCategoriesKeyboard(chatId, connectedSheet)); // Sending our message object to user
        } else if (NumberUtils.isParsable(message)) {
            execute(writeExpense(chatId, connectedSheet, callback, message)); // Sending our message object to user
        }
    }

    @Override
    public String getBotUsername() {
        try {
            return new CommonMethods().getConstant("bot.Username");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SendMessage sendCategoriesKeyboard(long chatID, Sheets connectedSheet) throws TelegramApiException { //SendMessage


        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Выберите категорию");


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        var numbersOfButtonsAndRows = getNumberOfRowsAndVariables(connectedSheet);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        
        for (int i = 0; i < numbersOfButtonsAndRows.get("rows"); i++) {
            
            List<InlineKeyboardButton> row = new ArrayList<>();
            
                for (int j = 0; j < 3; j++) { //В каждом ряду по 3 кнопки
                    InlineKeyboardButton inlineKeyboardButton = getInlineKeyboardButton(j+ 3 * i);
                    row.add(inlineKeyboardButton);
                }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
    }

    private @NotNull InlineKeyboardButton getInlineKeyboardButton(int j) {
        String buttonText = categories.get(j).get(0).toString();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(buttonText);
        inlineKeyboardButton.setCallbackData(String.valueOf(j));
        return inlineKeyboardButton;
    }

    public  Map<String, Integer> getNumberOfRowsAndVariables(Sheets connectedSheet){

        List<List<Object>> categories;

        try {
            categories = new ApiSheet().getCategories(connectedSheet);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        int numberOfRows;
        for (int i = 4; true;) { //минимум 4 ряда по три кнопки
            if (categories.size()%i==0){
                numberOfRows = i;
                break;
            } else {
                i++;
            }
        }
        Map<String, Integer> numberOfButtonsAndRows = new HashMap<>();
        numberOfButtonsAndRows.put("buttons",categories.size());
        numberOfButtonsAndRows.put("rows",numberOfRows);


        return numberOfButtonsAndRows;
    }

    public SendMessage chooseExpense(long chatID, String callback)
            throws TelegramApiException, URISyntaxException, IOException {

                SendMessage message = new SendMessage();
                message.setChatId(chatID);
                message.setText("Выбрана категория: " + callback +". Введите сумму");
                return message;

    }

    public SendMessage writeExpense(long chatID, Sheets connectedSheet, String callback, String messageWithSum)
            throws URISyntaxException, IOException {

        String result = new ApiSheet(connectedSheet).writeChosenExpense(connectedSheet, categories,callback, messageWithSum);
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText("Записана категория: " + callback + ".  Сумма: " + messageWithSum);
        return message;
    }

}

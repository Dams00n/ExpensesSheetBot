package com.ExpensesSheetBot.TelegramBotAPI;

import com.ExpensesSheetBot.GoogleSheetsAPI.ApiSheet;
import com.ExpensesSheetBot.GoogleSheetsAPI.SheetApiConnectionService;
import com.ExpensesSheetBot.Service.CommonMethods;
import com.google.api.services.sheets.v4.Sheets;
import lombok.SneakyThrows;
import org.apache.commons.lang3.math.NumberUtils;
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


        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()) {

            if(update.getMessage().getText().equals("/start")) {
                try {
                    execute(sendCategoriesKeyboard(chatId, connectedSheet)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if (NumberUtils.isParsable(message)) {
                try {
                    execute(writeExpense(chatId, connectedSheet, callback, message)); // Sending our message object to user
                } catch (TelegramApiException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } else if (update.hasCallbackQuery()) {
            callback = update.getCallbackQuery().getData();
            long chatID = update.getCallbackQuery().getMessage().getChatId();
            if (NumberUtils.isParsable(callback)) {
                try {
                    execute(chooseExpense(chatID, callback));
                } catch (TelegramApiException | URISyntaxException | IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {

            }
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
                    if (i==0) {
                        String buttonText = categories.get(j).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j));
                        row.add(inlineKeyboardButton);
                    } else if (i==1) {
                        String buttonText = categories.get(j+3).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3));
                        row.add(inlineKeyboardButton);
                    } else if (i==2) {
                        String buttonText = categories.get(j+3*i).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3*i));
                        row.add(inlineKeyboardButton);
                    } else if (i==3) {
                        String buttonText = categories.get(j+3*i).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3*i));
                        row.add(inlineKeyboardButton);
                    } else if (i==4) {
                        String buttonText = categories.get(j+3*i).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3*i));
                        row.add(inlineKeyboardButton);
                    } else if (i==5) {
                        String buttonText = categories.get(j+3*i).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3*i));
                        row.add(inlineKeyboardButton);
                    } else if (i==6) {
                        String buttonText = categories.get(j+3*i).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3*i));
                        row.add(inlineKeyboardButton);
                    } else if (i==7) {
                        String buttonText = categories.get(j+3*i).get(0).toString();
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText(buttonText);
                        inlineKeyboardButton.setCallbackData(String.valueOf(j+3*i));
                        row.add(inlineKeyboardButton);
                    }
                }

            rows.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        message.setReplyMarkup(inlineKeyboardMarkup);
        return message;
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

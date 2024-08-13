package com.ExpensesSheetBot.GoogleSheetsAPI;

import com.ExpensesSheetBot.Service.CommonMethods;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
//import lombok.SneakyThrows;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.*;

public class ApiSheet extends CommonMethods {
    Sheets connectedSheet;

    public ApiSheet() {
    }

    public ApiSheet(Sheets connectedSheet) {
        this.connectedSheet = connectedSheet;
    }

    public List<List<Object>> getCategories(Sheets connection) throws IOException, URISyntaxException {

        ValueRange response = connection.spreadsheets().values()
                .get(getConstant("spreadsheetId"), getConstant("range"))
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        }
        return values;
    }


    public String writeChosenExpense(Sheets connectedSheet,List<List<Object>> categories,String numberOfCategory, String sum) throws URISyntaxException, IOException {
        /*
        Нужно получить категории, потом перейти на другой лист,
        найти там последнюю строку и добавить в следующую запись
         */

        Spreadsheet sp = connectedSheet.spreadsheets()
                .get(getConstant("spreadsheetId")).execute();

        List<Sheet> sheetList = sp.getSheets();
        Sheet sheetToWrite = sheetList.get(Integer.parseInt(getConstant("expensesList.order")));

        String listId = String.valueOf(sheetToWrite.getProperties().getTitle());

        ValueRange appendBody = new ValueRange()
                .setValues(List.of(
                        Arrays.asList(new SimpleDateFormat("dd.MM.yyyy")
                                .format( new GregorianCalendar().getTime()),
                                categories.get(Integer.parseInt(numberOfCategory)).get(0), Integer.parseInt(sum))));

        AppendValuesResponse appendResult = connectedSheet.spreadsheets().values()
                .append(getConstant("spreadsheetId"),listId + "!A8", appendBody)
                .setValueInputOption("USER_ENTERED")
                //.setInsertDataOption("INSERT_ROWS") //если
                .setIncludeValuesInResponse(true)
                .execute();

        //ValueRange total = appendResult.getUpdates().getUpdatedData();
        return "Записано";
    }


}


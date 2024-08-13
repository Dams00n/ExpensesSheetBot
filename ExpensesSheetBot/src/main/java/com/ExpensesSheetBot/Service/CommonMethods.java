package com.ExpensesSheetBot.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommonMethods {

    public String getConstant(String categoryName) throws URISyntaxException, IOException {
        //Получаем константы из файла с константами
        //URL resource = CommonMethods.class.getResource("/constants");
        URI uri = getClass().getResource("/constants").toURI();

        if("jar".equals(uri.getScheme())){
            for (FileSystemProvider provider: FileSystemProvider.installedProviders()) {
                if (provider.getScheme().equalsIgnoreCase("jar")) {
                    try {
                        provider.getFileSystem(uri);
                    } catch (FileSystemNotFoundException e) {
                        // in this case, we need to initialize it first:
                        provider.newFileSystem(uri, Collections.emptyMap());
                    }
                }
            }
        }

        Path source = Paths.get(uri);

        //Paths.get(resource.toURI()).toFile();
        //Paths.get(Objects.requireNonNull(getClass()
        //                        .getClassLoader().getResource("constants"))
        //                .toURI()), StandardCharsets.UTF_8
        String[] content = Files.readAllLines(Paths.get(source.toUri())).toArray(new String[0]);

        Map<String, String> constantsMap = new HashMap<>();
        for (String keyValue : content) {
            String[] parts = keyValue.split("=");
            constantsMap.put(parts[0], parts[1]);
        }
        return constantsMap.get(categoryName);
    }
}

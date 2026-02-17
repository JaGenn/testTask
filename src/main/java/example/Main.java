package example;

import example.service.XmlParserService;
import example.service.XmlParserServiceImpl;

public class Main {

    private static final String XML_URL = "https://expro.ru/bitrix/catalog_export/export_Sai.xml";
    private static final XmlParserService parser = new XmlParserServiceImpl(XML_URL);

    public static void main(String[] args) {

        parser.update();
        present();
    }

    private static void present() {
        System.out.println();
        System.out.println("Просто расскажу как все устроено, без сканеров и команд с консоли");
        System.out.println("При создании сервиса и передачи ему xmlUrl, " +
                "внутри сразу происходит сканирование xml документа, парсинг нужных нам таблиц" +
                " и сканирование структуры каждой таблицы.\n" +
                "Все это сохраняется в кэше HashMap, где ключом является имя таблицы, а значение - коллекция с колонками этой таблицы");
        System.out.println("Дальше мы работаем со структурой из кэша, чтобы не парсить xml постоянно. Так же сразу происходит генерация DDL и создание таблиц в бд");
        System.out.println();
        System.out.println("Результат метода getTableNames()");
        var tableNames = parser.getTableNames();
        System.out.println(tableNames.toString());
        System.out.println();
        System.out.println("DDL создается динамически для каждой таблицы");
        System.out.println();
        for (var tableName : tableNames) {
            System.out.println(tableName + " -> " + parser.getTableDDL(tableName));
        }
        System.out.println();
        System.out.println("При вызове метода update() происходит парсинг содержимого каждой таблицы в xml и это все батчем вставляется в бд.\n" +
                "Если структура таблицы в бд и в xml перестали совпадать, то выбрасывается Exception. " +
                "Происходит сравнение самих записей по id и запись обновляется если изменилась с прошлого раза");
        System.out.println("Некоторые процессы логируются для наглядности. Для работы с xml использовал groovy скрипты");
    }
}

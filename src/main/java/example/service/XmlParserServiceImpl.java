package example.service;


import example.entity.Category;
import example.entity.Currency;
import example.entity.Offer;
import example.exception.TableIsNotSameException;
import example.groovy.GroovyXmlParser;
import example.repository.XmlTableRepository;
import example.util.SqlGeneratorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class XmlParserServiceImpl implements XmlParserService {

    private final String xmlUrl;
    private Map<String, Set<String>> cache;
    private final XmlTableRepository xmlTableRepository = new XmlTableRepository();

    public XmlParserServiceImpl(String xmlUrl) {
        this.xmlUrl = xmlUrl;
        updateCache();
        createTables();
    }

    private void updateCache() {
        this.cache = GroovyXmlParser.getStructure(xmlUrl);
        System.out.println("Xml structure has been parsed and cache has been updated");
    }

    private void createTables() {
        for (String tableName : getTableNames()) {
            String ddl = getTableDDL(tableName);
            xmlTableRepository.executeDDL(ddl);
        }
        System.out.println("Tables have been created");
    }

    @Override
    public List<String> getTableNames() {
        return new ArrayList<>(cache.keySet());
    }

    @Override
    public Set<String> getColumnNames(String tableName) {
        Set<String> columns = cache.get(tableName.toLowerCase());

        if (columns == null) {
            throw new IllegalArgumentException("Таблица не найдена в XML: " + tableName);
        }

        SqlGeneratorUtil.prepareColumnNames(tableName, columns);
        return columns;
    }

    @Override
    public String getTableDDL(String tableName) {
        Set<String> columns = cache.get(tableName.toLowerCase());

        if (columns == null || columns.isEmpty()) {
            throw new IllegalStateException("Нет данных для таблицы: " + tableName);
        }

        String ddl = SqlGeneratorUtil.createTableDDL(tableName, columns);
        return ddl;
    }

    @Override
    public void update() {
        updateCache();

        List<String> tableNames = getTableNames();

        for (String table : tableNames) {
            validateChanges(table);
        }
        for (String tableName : tableNames) {
            update(tableName);
        }

    }

    @Override
    public void update(String tableName) {
        switch (tableName) {

            case "currencies":
                List<Currency> currencies = GroovyXmlParser.parseCurrencies(xmlUrl);
                xmlTableRepository.upsertCurrencies(currencies);
                System.out.println("Currencies have been updated");
                break;

            case "categories":
                List<Category> categories = GroovyXmlParser.parseCategories(xmlUrl);
                xmlTableRepository.upsertCategories(categories);
                System.out.println("Categories have been updated");
                break;

            case "offers":
                List<Offer> offers = GroovyXmlParser.parseOffers(xmlUrl);
                xmlTableRepository.upsertOffers(offers);
                System.out.println("Offers have been updated");
                break;

            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }

    private void validateChanges(String tableName) {
        Set<String> xmlColumns = getColumnNames(tableName);
        Set<String> dbColumns = xmlTableRepository.getExistingColumns(tableName);

        if (xmlColumns.size() != dbColumns.size()) {
            throw new TableIsNotSameException("Table " + tableName + " has changed");
        }

        for (String col : xmlColumns) {
            if (!dbColumns.contains(col.toLowerCase())) {
                throw new TableIsNotSameException("Table " + tableName + " has changed");
            }
        }
    }

}


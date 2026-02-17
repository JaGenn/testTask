package example.service;

import java.util.List;
import java.util.Set;

public interface XmlParserService {

    List<String> getTableNames();

    String getTableDDL(String tableName);

    void update();

    void update(String tableName);

    Set<String> getColumnNames(String tableName);
}

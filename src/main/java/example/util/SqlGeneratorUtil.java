package example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SqlGeneratorUtil {

    public static void prepareColumnNames(String tableName, Set<String> columns) {

        if (tableName.equalsIgnoreCase("categories") && columns.contains("parentid")) {
            columns.remove("parentid");
            columns.add("parent_id");
        }
        if (tableName.equalsIgnoreCase("offers") && columns.contains("vendorcode")) {
            columns.remove("vendorcode");
            columns.add("vendor_code");
        }
        if (tableName.equalsIgnoreCase("offers") && columns.contains("categoryid")) {
            columns.remove("categoryid");
            columns.add("category_id");
        }
        if (tableName.equalsIgnoreCase("offers") && columns.contains("currencyid")) {
            columns.remove("currencyid");
            columns.add("currency_id");
        }
    }

    public static String createTableDDL(String tableName, Set<String> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        List<String> definitions = new ArrayList<>();

        boolean hasId = columns.contains("id");
        boolean hasVendorCode = columns.contains("vendorcode") || columns.contains("vendor_code");

        for (String col : columns) {
            String realCol = col;
            if ("parentid".equals(col) && tableName.equalsIgnoreCase("categories")) {
                realCol = "parent_id";
            }
            if ("vendorcode".equals(col) && tableName.equalsIgnoreCase("offers")) {
                realCol = "vendor_code";
            }
            if ("categoryid".equals(col) && tableName.equalsIgnoreCase("offers")) {
                realCol = "category_id";
            }
            if ("currencyid".equals(col) && tableName.equalsIgnoreCase("offers")) {
                realCol = "currency_id";
            }

            String type;
            if ("id".equals(col) && tableName.equalsIgnoreCase("currencies")) {
                type = "VARCHAR(16)";
            } else if ("currency_id".equals(realCol) && tableName.equalsIgnoreCase("currencies")) {
                type = "VARCHAR(16)";
            } else {
                type = guessTypeFromName(realCol);
            }

            definitions.add("    " + realCol + " " + type);
        }

        if (hasVendorCode) {
            definitions.add("    PRIMARY KEY (vendor_code)");
        } else if (hasId) {
            definitions.add("    PRIMARY KEY (id)");
        }

        sb.append(String.join(",\n", definitions));
        sb.append("\n);");

        return sb.toString();
    }

    private static String guessTypeFromName(String col) {
        return switch (col.toLowerCase()) {
            case "id", "category_id", "parent_id", "count" -> "INT";
            case "price" -> "DECIMAL(18,2)";
            case "available", "adult", "manufacturer_warranty" -> "BOOLEAN";
            case "currency_id" -> "VARCHAR(16)";
            case "picture", "description" -> "TEXT";
            default -> "VARCHAR(255)";
        };
    }
}

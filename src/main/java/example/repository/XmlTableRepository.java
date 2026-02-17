package example.repository;

import example.entity.Category;
import example.entity.Currency;
import example.entity.Offer;
import example.exception.DataBaseOperationException;
import example.util.DataBase;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class XmlTableRepository {


    public void executeDDL(String ddl) {
        try (Connection connection = DataBase.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(ddl);

        } catch (SQLException e) {
            throw new DataBaseOperationException("DDL execution failed", e);
        }
    }

    public void upsertCurrencies(List<Currency> currencies) {

        String sql = """
                    INSERT INTO currencies (id, rate)
                    VALUES (?, ?)
                    ON CONFLICT (id)
                    DO UPDATE SET rate = EXCLUDED.rate
                """;

        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Currency currency : currencies) {

                ps.setString(1, currency.getId());
                ps.setBigDecimal(2, currency.getRate());

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new DataBaseOperationException("Updating currencies failed", e);
        }
    }

    public void upsertCategories(List<Category> categories) {

        String sql = """
                    INSERT INTO categories (id, parent_id, value)
                    VALUES (?, ?, ?)
                    ON CONFLICT (id)
                    DO UPDATE SET
                        parent_id = EXCLUDED.parent_id,
                        value = EXCLUDED.value
                """;

        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Category category : categories) {

                ps.setInt(1, category.getId());
                ps.setObject(2, category.getParentId(), Types.INTEGER);
                ps.setString(3, category.getValue());

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new DataBaseOperationException("Updating categories failed", e);
        }


    }

    public void upsertOffers(List<Offer> offers) {

        String sql = """
                    INSERT INTO offers (
                        id, available, url, price, picture,
                        name, vendor, description, count,
                        vendor_code, category_id, currency_id
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (vendor_code)
                    DO UPDATE SET
                        available = EXCLUDED.available,
                        url = EXCLUDED.url,
                        price = EXCLUDED.price,
                        picture = EXCLUDED.picture,
                        name = EXCLUDED.name,
                        vendor = EXCLUDED.vendor,
                        description = EXCLUDED.description,
                        count = EXCLUDED.count,
                        category_id = EXCLUDED.category_id,
                        currency_id = EXCLUDED.currency_id
                """;

        try (Connection connection = DataBase.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (Offer offer : offers) {

                ps.setInt(1, offer.getId());
                ps.setBoolean(2, offer.isAvailable());
                ps.setString(3, offer.getUrl());
                ps.setBigDecimal(4, offer.getPrice());
                ps.setString(5, offer.getPicture());
                ps.setString(6, offer.getName());
                ps.setString(7, offer.getVendor());
                ps.setString(8, offer.getDescription());
                ps.setInt(9, offer.getCount());
                ps.setString(10, offer.getVendorCode());
                ps.setInt(11, offer.getCategoryId());
                ps.setString(12, offer.getCurrencyId());

                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new DataBaseOperationException("Updating categories failed", e);
        }


    }

    public Set<String> getExistingColumns(String tableName) {
        Set<String> columns = new HashSet<>();
        String query = "SELECT column_name FROM information_schema.columns WHERE table_name = ?";

        try (Connection connection = DataBase.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, tableName);

            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    columns.add(resultSet.getString("column_name").toLowerCase());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return columns;
    }
}



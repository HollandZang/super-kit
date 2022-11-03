package com.holland.kit.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class SqlKit {
    public static List<Map<String, ?>> exec(Connection connection, String sql, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
//        try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            for (int i = 0; i < params.length; i++)
                statement.setObject(i + 1, params[i]);
            if (statement.execute()) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    return genResult(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<Map<String, ?>> genResult(ResultSet resultSet) throws SQLException {
//        int row = countRow(resultSet);
//        if (row == 0) return new ArrayList<>();

        List<Map<String, ?>> list = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                Object v          = resultSet.getObject(columnName);
                map.put(columnName, v);
            }
            list.add(map);
        }
        return list;
    }

    public static int countRow(ResultSet resultSet) throws SQLException {
        int row;
        resultSet.last();
        row = resultSet.getRow();
        resultSet.first();
        return row;
    }
}

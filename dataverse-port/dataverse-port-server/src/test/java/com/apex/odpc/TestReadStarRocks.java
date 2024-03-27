package com.apex.odpc;

import java.sql.*;

/**
 * @version : v1.0
 * @projectName : nexus-odpc
 * @package : com.apex.odpc
 * @className : TestReadStarRocks
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/4/12 16:09
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public class TestReadStarRocks {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        testCreateTable();

        //testRead();

        //testInsert();
    }

    public static void testCreateTable()
            throws SQLException, ClassNotFoundException {
        Connection connection = getConn();
        PreparedStatement statement = null;
        for (int i = 0; i < 10; i++) {
            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS sr_member3 (\n" +
                    "    sr_id            INT,\n" +
                    "    name             STRING,\n" +
                    "    city_code        INT,\n" +
                    "    reg_date         DATE,\n" +
                    "    verified         BOOLEAN\n" +
                    ")\n" +
                    "PARTITION BY RANGE(reg_date)\n" +
                    "(\n" +
                    "    PARTITION p1 VALUES [('2022-03-13'), ('2022-03-14')),\n" +
                    "    PARTITION p2 VALUES [('2022-03-14'), ('2022-03-15')),\n" +
                    "    PARTITION p3 VALUES [('2022-03-15'), ('2022-03-16')),\n" +
                    "    PARTITION p4 VALUES [('2022-03-16'), ('2022-03-17')),\n" +
                    "    PARTITION p5 VALUES [('2022-03-17'), ('2022-03-18'))\n" +
                    ")\n" +
                    "DISTRIBUTED BY HASH(city_code)\n" +
                    "PROPERTIES(\n" +
                    "    \"replication_num\" = \"1\"\n" +
                    ");");
            statement.executeUpdate();
        }
        //statement.executeBatch();
        //connection.commit();

        statement.close();
        connection.close();
    }

    public static void testInsert() throws SQLException, ClassNotFoundException {
        Connection connection = getConn();
        //connection.setAutoCommit(false);
        PreparedStatement statement = null;
        for (int i = 0; i < 10; i++) {
            statement = connection.prepareStatement("insert into sr_member(sr_id, `name`, city_code, reg_date, verified) WITH LABEL demo2 values (?,?,?,?,?)");
            statement.setInt(1, i);
            statement.setString(2, "Name-of-Doris" + i);
            statement.setInt(3, 999 + i);
            statement.setDate(4, new Date(System.currentTimeMillis()));
            statement.setBoolean(5, true);
            statement.execute();
        }
        //statement.executeBatch();
        //connection.commit();
        
        statement.close();
        connection.close();
    }

    public static void testRead()
            throws ClassNotFoundException, SQLException {
        Connection connection = getConn();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from sr_member");

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Integer id = resultSet.getInt("sr_id");
            String name = resultSet.getString("name");
            System.out.println(id + " : " + name);
        }

        preparedStatement.close();
        connection.close();
    }

    /**
     * 获取Connection
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConn() throws ClassNotFoundException, SQLException {
        String jdbcUrl = "jdbc:mysql://10.25.19.1:9030/sr_hub";
        String userName = "root";
        String password = "";

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(jdbcUrl, userName, password);
        return connection;
    }
}

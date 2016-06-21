package com.ai.app.aitask.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oracle.jdbc.pool.OracleConnectionCacheImpl;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

public class Oracle {
    public void test() throws SQLException {
        //        DriverManager.registerDriver(new OracleDriver());
        OracleConnectionPoolDataSource source = new OracleConnectionPoolDataSource();
        source.setDatabaseName("oracle pool");

        source.setURL("jdbc:oracle:thin:@10.1.195.100:1521:ats");
        source.setUser("pageframe");
        source.setPassword("pageframe");

        OracleConnectionCacheImpl cache = new OracleConnectionCacheImpl(source);
        cache.setMinLimit(5);
        cache.setMaxLimit(10);
        cache.setCacheScheme(1);

        Connection conn = cache.getConnection();
        conn.setAutoCommit(false);
        //SELECT table_name FROM user_tables
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery("SELECT * FROM user_tables");
        int columns = result.getMetaData().getColumnCount();
        for (int i = 1; i < columns; i++) {
            System.out.print(result.getMetaData().getColumnName(i) + "\t");
        }
        System.out.println();
        for (; result.next();) {
            for (int i = 1; i < columns; i++) {
                System.out.print(result.getString(i) + "\t");
            }
            System.out.println();
        }
        //        System.out.println(result.getRow());
        //        result.next();
        //        System.out.println(Arrays.toString((Object[]) result.getArray(1).getArray()));
        result.close();
        stmt.close();
        conn.close();
    }
    public static void main(String[] args) {
        try {
            new Oracle().test();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

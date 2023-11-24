package org.example.Jdbc;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class JdbcMain {

    public static void main(String[] args) {

        Connection connection = null;
        PreparedStatement ps = null;
        PreparedStatement psInsert = null;
        ResultSet rs = null;


        String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
        String jdbcurl = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
        String username = "hr";
        String pasword = "hr";

        try {
            String sql = "SELECT id, ad, soyad, maas FROM person_gunel ";
            String insertSql = "INSERT INTO person_gunel (id, ad, soyad, maas) VALUES (person_gunel_seq.nextval, ?, ?, ?)";
            String updateSql = "update person_gunel set maas = maas + ? where maas <= ? ";
            String deleteSql = "delete from person_gunel  where maas <=? ";
            try {
                connection = DriverManager.getConnection(jdbcurl, username, pasword);
                connection.setAutoCommit(false);

                Scanner sc = new Scanner(System.in);
                System.out.print("Enter name: ");
                String name = sc.next();
                System.out.print("Enter surname: ");
                String surname = sc.next();
                System.out.print("Enter salary: ");
                BigDecimal salary = sc.nextBigDecimal();

                psInsert = connection.prepareStatement(insertSql);

                psInsert.setString(1, name);
                psInsert.setString(2, surname);
                psInsert.setBigDecimal(3, salary);
                int count = psInsert.executeUpdate();
                System.out.println(count + " person");
                System.out.println("Max salary limit:");
                BigDecimal limit = sc.nextBigDecimal();
                System.out.println("Plus: ");
                BigDecimal plus = sc.nextBigDecimal();

                ps = connection.prepareStatement(deleteSql);
                System.out.println("Do you want to delete something?");
                limit = sc.nextBigDecimal();
                ps.setBigDecimal(1, limit);
                count = ps.executeUpdate();
                if (count > 0) {
                    System.out.println(count + " . ");
                }
                ps = connection.prepareStatement(updateSql);
                ps.setBigDecimal(1, plus);
                ps.setBigDecimal(2, limit);
                count = ps.executeUpdate();
                ps = connection.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    long id = rs.getLong("id");
                    name = rs.getString("ad");
                    surname = rs.getString("soyad");
                    salary = rs.getBigDecimal("maas");
                    System.out.printf("%d %s %s %.2f\n", id, name, surname, salary);
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            JdbcUtility.close(rs, ps, connection);
        }
    }
}

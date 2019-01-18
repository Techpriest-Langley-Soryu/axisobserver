package Server;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class conn {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet res;


    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\Techpriest\\Desktop\\Client-Server_Test_Soft\\TestCSDB.db");
        System.out.println("База Подключена!");
        if (conn == null){System.out.println("База не подключена");}
    }
    // --------Заполнение таблицы--------
    public static void WriteDB(String SN) throws SQLException
    {
        conn.createStatement().execute("INSERT INTO 'SerialNumber' ('SerNum') VALUES ('"+SN+"'); ");
        System.out.println("Таблица заполнена");
    }
    // -------- Вывод таблицы--------
    public static int ReadDB(String key) throws SQLException {
        String sql = "SELECT * FROM Keys WHERE Key = '" + key + "';";
        res = conn.createStatement().executeQuery(sql);
        String rep = "none";
        while (res.next()) {
            rep = res.getString("Key");
        }
        if (rep == "none") {
            return 0;
        } else {
            return 1;
        }

        // System.out.println("Таблица выведена");
    }

    // --------Закрытие--------
    public static void CloseDB() throws SQLException
    {
        conn.close();
        conn.createStatement().close();
//        resSet.close();

        System.out.println("Соединения закрыты");
    }

}

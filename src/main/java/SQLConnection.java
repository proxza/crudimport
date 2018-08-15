import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.sqlite.SQLiteException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class SQLConnection {

    private Connection connection;
    private static Statement state;
    private static final String TMP_DIR = System.getenv("APPDATA") + "\\CRUDiTMP\\"; // Путь к временной папке
    private static File settingsFile = new File(TMP_DIR + "crudiSettings.cfg"); // Имя файла с настройками
    private static String baseFolder = TMP_DIR;

    public SQLConnection(){
        connection = Connect();
        try {
            state = connection.createStatement();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Connection Connect() {
        try {

            // Считываем файл настроек
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFile), "cp1251"));
            String line;
            while ((line = bf.readLine()) != null) {
                baseFolder = line;
            }

            Class.forName("org.sqlite.JDBC");
            Connection connect = DriverManager.getConnection("jdbc:sqlite:" + baseFolder + "\\crudiBase.db");

            return connect;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ObservableList<String> getUserListFromDB(){
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM Users";

            ResultSet resultSet = state.executeQuery(query);
            while (resultSet.next()) {
                obList.add(resultSet.getString("username"));
            }

            return obList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ObservableList<String> getCompanyListFromDB(){
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM Company";

            ResultSet resultSet = state.executeQuery(query);
            while (resultSet.next()) {
                obList.add(resultSet.getString("companyName"));
            }

            return obList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addItem(int number, String name, String desc, int price, String buy, String crash, String place, String idCompany, String user) {

        try{

            String query = "INSERT INTO Items(number, name, desc, price, buy, crash, place, idCompany, whoadd) VALUES ("
                    + number + ", '"
                    + name + "', '"
                    + desc + "', "
                    + price + ", '"
                    + buy + "', '"
                    + crash + "', '"
                    + place + "', '"
                    + idCompany + "', '"
                    + user + "')";

            state.addBatch(query);
            state.executeBatch();

        } catch (BatchUpdateException e) {
            // error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Предмет с таким номером (" + number + ") уже есть в базе!");
            alert.setTitle("Ошибка!");
            alert.showAndWait();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                state.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

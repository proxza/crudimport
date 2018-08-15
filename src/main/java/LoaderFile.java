import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

public class LoaderFile implements Initializable {

    private File folder;
    private ArrayList<String> listToDB = new ArrayList<>();

    @FXML private Label status;
    @FXML private ComboBox<String> userListBox;
    @FXML private ComboBox<String> companyList;
    @FXML Button btnGetFolder;
    @FXML Button btnGo;


    public void initialize(URL location, ResourceBundle resources) {

        SQLConnection sqlStart = new SQLConnection();

        try {

            userListBox.setItems(SQLConnection.getUserListFromDB());
            companyList.setItems(SQLConnection.getCompanyListFromDB());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void getExcelFile(){

        final FileChooser fileChooser = new FileChooser();

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            folder = file;
            status.setText("Файл выбран, можно загружать в базу!");
        } else {
            // error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Вы не выбрали файл!");
            alert.setTitle("Ошибка!");
            alert.showAndWait();
        }

    }

    @FXML
    private void readExcelFileAndSendToDB() throws Exception {

        listToDB.clear(); // Чистим лист чтобы не было дублей повторных

        // Обработка
        if (folder == null) {
            // error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Вы не выбрали файл!");
            alert.setTitle("Ошибка!");
            alert.showAndWait();
            return;
        } else if (userListBox.getValue() == null) {
            // error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Вы не выбрали пользователя!");
            alert.setTitle("Ошибка!");
            alert.showAndWait();
            return;
        } else if (companyList.getValue() == null) {
            // error
            Alert alert = new Alert(Alert.AlertType.ERROR, "Вы не выбрали предприятие!");
            alert.setTitle("Ошибка!");
            alert.showAndWait();
            return;
        }

        btnGo.setText("LOL");


        FileInputStream file = new FileInputStream(folder);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet mySheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = mySheet.iterator();


        while (iterator.hasNext()) {
            Row currentRow = iterator.next();

            // LOLWHAT
            String invNumber = currentRow.getCell(0).getStringCellValue().replaceAll("\\s", " ");
            String name = currentRow.getCell(1).getStringCellValue().replaceAll("\\s", " ");
            String desc = currentRow.getCell(2).getStringCellValue().replaceAll("\\s", " ");
            String price = "" + (int) currentRow.getCell(3).getNumericCellValue();
            String buy = currentRow.getCell(4).getStringCellValue();
            String crash = currentRow.getCell(5).getStringCellValue();
            String place = currentRow.getCell(6).getStringCellValue().replaceAll("\\s", " ");

            listToDB.add(invNumber + ":" + name + ":" + desc + ":" + price + ":" + buy + ":" + crash + ":" + place + ":" + companyList.getValue() + ":" + userListBox.getValue());

        }
        file.close();

        convertListToSQL(listToDB);
    }

    private void convertListToSQL(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String[] tmp = list.get(i).split(":");

            // разбиваем время
            String[] timeBuy = tmp[4].split("\\.");
            String buyTime = timeBuy[0] + "-" + timeBuy[1] + "-20" + timeBuy[2];

            String crashTime = "";
            if (!tmp[5].isEmpty()) {
                String[] timeCrash = tmp[5].split("\\.");
                crashTime = timeCrash[0] + "-" + timeCrash[1] + "-20" + timeCrash[2];
            }

            // обрабатываем запрос
            SQLConnection.addItem(Integer.parseInt(tmp[0]), tmp[1], tmp[2], Integer.parseInt(tmp[3]), buyTime, crashTime, tmp[6], tmp[7], tmp[8]);
        }

        status.setText("Файл успешно загружен в базу :)");

    }
}

module com.example.kursach {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens spbstu.ru.molchanovIr.kursach to javafx.fxml;
    exports spbstu.ru.molchanovIr.kursach;
}
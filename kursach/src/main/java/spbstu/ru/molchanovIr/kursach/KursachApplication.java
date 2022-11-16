package spbstu.ru.molchanovIr.kursach;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

public class KursachApplication extends Application {

    static public Properties properties;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(KursachApplication.class.getResource("kursach-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("kursach");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("C:/Users/User/Desktop/архитектура программных систем/kursach2/kursach/src/config.cfg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ApplicationController.isStepMode = Boolean.parseBoolean(properties.getProperty("STEP_MODE"));
        GeneratorRequests.countRequests = Integer.parseInt(properties.getProperty("COUNT_REQUESTS"));
        launch();
    }
}
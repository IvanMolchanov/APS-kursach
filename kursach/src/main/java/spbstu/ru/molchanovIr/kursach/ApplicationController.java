package spbstu.ru.molchanovIr.kursach;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ApplicationController {
    static volatile boolean isStepMode;
    @FXML
    private Button startButton;
    @FXML
    private TableView<ResultsStructure> tableResults;
    static volatile boolean nextStep = true;

    class MyProgram implements Runnable {
        @Override
        public void run() {
            StatisticsTable statisticsTable = new StatisticsTable();
            TableGUI tableGUI = new TableGUI();
            Buffer buffer = new Buffer();
            Devices devices = new Devices();
            GeneratorRequests generator = new GeneratorRequests();
            Request newRequest = generator.getNext(Instant.ofEpochMilli(0), ChronoUnit.MILLIS, ChronoUnit.MILLIS);
            tableGUI.update(tableResults, Instant.ofEpochMilli(0), null, buffer, devices, null, null);
            Instant readyTime = null;
            while (generator.hasNext()) {
                if (readyTime != null && newRequest.startWork.compareTo(readyTime) > 0) {
                    Request readyRequest = devices.getEarliestReady();
                    statisticsTable.add(readyRequest);
                    tableGUI.update(tableResults, readyRequest.finishWork, null, buffer, devices, readyRequest, null);
                    Request acceptRequest = buffer.pop();
                    if (acceptRequest != null) {
                        devices.insert(acceptRequest, readyRequest.finishWork);
                        tableGUI.update(tableResults, readyRequest.finishWork, null, buffer, devices, null, null);
                    }
                } else {
                    tableGUI.update(tableResults, newRequest.startWork, newRequest, buffer, devices, null, null);
                    Request rejectRequest = buffer.insert(newRequest);
                    if (rejectRequest != null) {
                        rejectRequest.startWorkOnDevice = newRequest.startWork;
                        statisticsTable.add(rejectRequest);
                        tableGUI.update(tableResults, newRequest.startWork, null, buffer, devices, null, rejectRequest);
                    } else if (devices.isNotFull()) {
                        Request acceptRequest = buffer.pop();
                        devices.insert(acceptRequest, acceptRequest.startWork);
                        tableGUI.update(tableResults, acceptRequest.startWork, null, buffer, devices, null, null);
                    } else {
                        tableGUI.update(tableResults, newRequest.startWork, null, buffer, devices, null, null);
                    }
                    newRequest = generator.getNext(newRequest.startWork, ChronoUnit.MILLIS, ChronoUnit.MILLIS);
                }
                readyTime = devices.getEarliestReadyTime();
            }
            readyTime = devices.getEarliestReadyTime();
            Instant lastMoment = readyTime;
            while (readyTime != null) {
                Request readyRequest = devices.getEarliestReady();
                statisticsTable.add(readyRequest);
                tableGUI.update(tableResults, readyRequest.finishWork, null, buffer, devices, readyRequest, null);
                Request acceptRequest = buffer.pop();
                if (acceptRequest != null) {
                    devices.insert(acceptRequest, readyRequest.finishWork);
                    tableGUI.update(tableResults, readyRequest.finishWork, null, buffer, devices, null, null);
                }
                lastMoment = readyTime;
                readyTime = devices.getEarliestReadyTime();
            }
            statisticsTable.crate(lastMoment);
        }
    }

    @FXML
    protected void onStepButtonClick() {
        nextStep = true;
    }

    @FXML
    protected void onContinueButtonClick() {
        isStepMode = false;
    }

    @FXML
    protected void onStartButtonClick() {
        TableColumn<ResultsStructure, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        tableResults.getColumns().add(timeColumn);

        TableColumn<ResultsStructure, String> inputColumn = new TableColumn<>("Input");
        inputColumn.setCellValueFactory(new PropertyValueFactory<>("input"));
        tableResults.getColumns().add(inputColumn);

        for (int i = 0; i < Buffer.size; ++i) {
            TableColumn<ResultsStructure, String> bufferColumn = new TableColumn<>("Buffer" + i);
            int columnNumber = i;
            bufferColumn.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ResultsStructure, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ResultsStructure, String> p) {
                            return new SimpleStringProperty(p.getValue().getBuffer()[columnNumber]);
                        }
                    }
            );
            tableResults.getColumns().add(bufferColumn);
        }
        for (int i = 0; i < Devices.size; ++i) {
            TableColumn<ResultsStructure, String> deviceColumn = new TableColumn<>("Device" + i);
            int columnNumber = i;
            deviceColumn.setCellValueFactory(
                    new Callback<TableColumn.CellDataFeatures<ResultsStructure, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ResultsStructure, String> p) {
                            return new SimpleStringProperty(p.getValue().getDevices()[columnNumber]);
                        }
                    }
            );
            tableResults.getColumns().add(deviceColumn);
        }
        TableColumn<ResultsStructure, String> outputColumn = new TableColumn<>("Output");
        outputColumn.setCellValueFactory(new PropertyValueFactory<>("output"));
        tableResults.getColumns().add(outputColumn);

        TableColumn<ResultsStructure, String> rejectColumn = new TableColumn<>("Reject");
        rejectColumn.setCellValueFactory(new PropertyValueFactory<>("reject"));
        tableResults.getColumns().add(rejectColumn);
        Thread newThread = new Thread(new MyProgram());
        newThread.start();
        startButton.setDisable(true);
    }
}
package spbstu.ru.molchanovIr.kursach;

import javafx.scene.control.TableView;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.LinkedList;

public class TableGUI {
    private static String requestToString(Request request) {
        return request == null ? "null" : request.number.toString();
    }

    private final static Long secondsInMinute = 60L;
    private final static Long secondsInHour = secondsInMinute * 60L;

    public static String instantToString(Instant now) {
        return String.format("%d:%2d:%2d",
                        now.getEpochSecond() % secondsInHour / secondsInMinute,
                        now.getEpochSecond() % secondsInMinute,
                        now.get(ChronoField.MILLI_OF_SECOND)/10)
                .replace(' ', '0');
    }

    public void update(TableView<ResultsStructure> table, Instant now, Request input, Buffer buffer, Devices devices, Request output, Request reject) {
        if (ApplicationController.isStepMode) {
            while (!ApplicationController.nextStep && ApplicationController.isStepMode) {
            }
            ApplicationController.nextStep = false;
        }
        String timeT = instantToString(now);
        String inputT = requestToString(input);
        String outputT = requestToString(output);
        String rejectT = requestToString(reject);
        LinkedList<String> bufferT = new LinkedList<>();
        LinkedList<String> devicesT = new LinkedList<>();
        for (int i = 0; i < Buffer.size; ++i) {
            bufferT.add(requestToString(buffer.getRequest(i)));
        }
        for (int i = 0; i < Devices.size; ++i) {
            devicesT.add(requestToString(devices.getRequest(i)));
        }
        ResultsStructure res = new ResultsStructure(timeT, inputT, bufferT, devicesT, outputT, rejectT);
        synchronized (table) {
            table.getItems().add(res);
        }
        /*System.out.printf("%10s", timeT);
        System.out.printf("%5s", inputT);
        bufferT.forEach(a -> System.out.printf("%5s", a));
        devicesT.forEach(a -> System.out.printf("%5s", a));
        System.out.printf("%5s", outputT);
        System.out.printf("%5s", rejectT);
        if (output != null) {
            System.out.printf("%5s", output.source);
            System.out.printf("%10s\n", instantToString(output.startWork));
        } else if (reject != null) {
            System.out.printf("%5s", reject.source);
            System.out.printf("%10s\n", instantToString(reject.startWork));
        } else System.out.println();*/
    }
}

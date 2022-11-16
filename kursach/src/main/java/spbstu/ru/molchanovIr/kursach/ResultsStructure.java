package spbstu.ru.molchanovIr.kursach;

import java.util.List;

public class ResultsStructure {
    private String time;
    private String input;
    private String[] buffer;
    private String[] devices;
    private String output;
    private String reject;

    public ResultsStructure(String time, String input, List<String> buffer, List<String> devices,
                            String output, String reject) {
        this.time = time;
        this.input = input;
        this.buffer = buffer.toArray(new String[0]);
        this.devices = devices.toArray(new String[0]);
        this.output = output;
        this.reject = reject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String[] getBuffer() {
        return buffer;
    }

    public void setBuffer(String[] buffer) {
        this.buffer = buffer;
    }

    public String[] getDevices() {
        return devices;
    }

    public void setDevices(String[] devices) {
        this.devices = devices;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getReject() {
        return reject;
    }

    public void setReject(String reject) {
        this.reject = reject;
    }
}

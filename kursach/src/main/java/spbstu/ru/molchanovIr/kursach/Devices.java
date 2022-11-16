package spbstu.ru.molchanovIr.kursach;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class Devices {
    public static int size;
    private Request[] buffer;
    private int index;
    private int contains;

    static {
        size = Integer.parseInt(KursachApplication.properties.getProperty("DEVICE_COUNT"));
    }

    {
        buffer = new Request[size];
        index = 0;
        contains = 0;
    }

    public boolean insert(Request request, Instant now) {
        if (contains == size) {
            return false;
        }
        int saveIndex = index;
        while (buffer[index] != null && (index != (saveIndex - 1 + size) % size)) {
            index = (index + 1) % size;
        }
        request.finishWork = now.plusNanos(request.duration.getNano()
                + request.duration.getEpochSecond() * 1000_000_000L);
        request.numberDevice = index;
        request.startWorkOnDevice = now;
        buffer[index] = request;
        index = (index + 1) % size;
        contains++;
        return true;
    }

    private int getEarliestReadyInd() {
        if (contains == 0) {
            return -1;
        }
        int resInd = 0;
        for (int i = 0; i < size; ++i) {
            if (buffer[i] != null && (buffer[resInd] == null
                    || buffer[i].finishWork.compareTo(buffer[resInd].finishWork) < 0)) {
                resInd = i;
            }
        }
        return resInd;
    }

    public Instant getEarliestReadyTime() {
        int resInd = getEarliestReadyInd();
        if (resInd < 0) {
            return null;
        }
        return buffer[resInd].finishWork;
    }

    public Request getEarliestReady() {
        int resInd = getEarliestReadyInd();
        if (resInd < 0) {
            return null;
        }
        Request tmp = buffer[resInd];
        buffer[resInd] = null;
        contains--;
        return tmp;
    }

    public boolean isNotFull() {
        return contains != size;
    }

    public boolean isNotEmpty() {
        return contains != 0;
    }

    Request getRequest(int ind) {
        if (buffer[ind] == null) {
            return null;
        } else {
            return buffer[ind];
        }
    }
}

package spbstu.ru.molchanovIr.kursach;

public class Buffer {
    private int index;
    private final Request[] buffer;

    public static int size;

    static {
        size = Integer.parseInt(KursachApplication.properties.getProperty("BUFFER_SIZE"));
    }

    {
        buffer = new Request[size];
        index = 0;
    }

    //возвращает заявку, на которую пришел отказ
    public Request insert(Request newRequest) {
        int saveIndex = index;
        int oldRequestInd = index;
        while (buffer[index] != null && (index != (saveIndex - 1 + size) % size)) {
            index = (index + 1) % size;
            if (buffer[index] != null && (buffer[oldRequestInd] == null
                    || buffer[oldRequestInd].startWork.compareTo(buffer[index].startWork) > 0)) {
                oldRequestInd = index;
            }
        }
        if (buffer[index] == null) {
            buffer[index] = newRequest;
            index = (index + 1) % size;
            return null;
        }
        Request oldRequest = buffer[oldRequestInd];
        buffer[oldRequestInd] = newRequest;
        index = oldRequestInd;
        index = (index + 1) % size;
        return oldRequest;
    }

    public Request pop() {
        int ind = 0;
        for (int i = 0; i < size; ++i) {
            if (buffer[i] != null
                    && (buffer[ind] == null
                    || buffer[ind].startWork.compareTo(buffer[i].startWork) < 0)) {
                ind = i;
            }
        }
        if (buffer[ind] != null) {
            Request res = buffer[ind];
            buffer[ind] = null;
            return res;
        } else {
            return null;
        }
    }

    Request getRequest(int ind) {
        return buffer[ind];
    }
}

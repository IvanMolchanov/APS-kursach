package spbstu.ru.molchanovIr.kursach;

import java.time.Instant;

public class Request {
    public Integer number;
    public Instant startWork;
    public Instant finishWork;
    public Integer source;
    public Instant duration;
    public Instant startWorkOnDevice;
    public Integer numberDevice;

    public Request(int number, Instant startWork, Instant duration, Integer source) {
        this.source = source;
        this.number = number;
        this.startWork = startWork;
        finishWork = null;
        this.duration = duration;
    }
}

package spbstu.ru.molchanovIr.kursach;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GeneratorRequests {
    private final Random key;
    private int number;
    static int countRequests;
    private final int boundDiffer = 1000;
    private final long boundDuration = 1000;
    private final long startDiffer = 5300;
    private final long startDuration = 20000;
    static public int countSources;

    {
        number = 1;
        key = new Random();
        countSources = Integer.parseInt(KursachApplication.properties.getProperty("COUNT_SOURCES"));
    }

    Request getNext(Instant now, ChronoUnit differUnit, ChronoUnit durationUnit) {
        long differ = startDiffer + key.nextInt(boundDiffer);
        double rnd2 = key.nextDouble();
        long duration = startDuration + (long) (Math.log(1 - rnd2) * boundDuration);
        return new Request(number++,
                now.plus(differ, differUnit),
                Instant.EPOCH.plus(duration, durationUnit),
                key.nextInt(countSources));
    }

    boolean hasNext() {
        return number <= countRequests + 1;
    }
}

package org.example.trainingapp.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;


@Component
public class TrainingExecutionMetrics {

    private final Timer timer;

    public TrainingExecutionMetrics(MeterRegistry registry) {               //  training creation time metric
        this.timer = registry.timer("training.creation.timer");
    }

    public <T> T record(Supplier<T> supplier) {
        return timer.record(supplier);
    }

    public void record(Runnable runnable) {
        timer.record(runnable);
    }
}


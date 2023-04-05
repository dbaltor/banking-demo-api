package online.dbaltor.demoapi.util;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicBigDecimal{
    private final AtomicReference<BigDecimal> valueHolder = new AtomicReference<>();

    public AtomicBigDecimal(BigDecimal amount) {
        valueHolder.set(amount);
    }

    public BigDecimal addAndGet(BigDecimal addend) {
        return valueHolder.updateAndGet( augend -> augend.add(addend));
    }
}

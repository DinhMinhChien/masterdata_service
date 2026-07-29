package vn.com.ssv.master_data.common.observability;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Logger {

    public static void logMemory(String step) {
        Runtime runtime = Runtime.getRuntime();

        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long total = runtime.totalMemory() / (1024 * 1024);
        long max = runtime.maxMemory() / (1024 * 1024);

        log.info("[{}]", step);
        log.info("Used  : {} MB", used);
        log.info("Total : {} MB", total);
        log.info("Max   : {} MB", max);
    }
}

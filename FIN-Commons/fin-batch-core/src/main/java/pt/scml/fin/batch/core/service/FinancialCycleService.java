package pt.scml.fin.batch.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FinancialCycleService {
    
    private final FinUtilsService finUtilsService;

    public FinancialCycleService(FinUtilsService finUtilsService) {
        this.finUtilsService = finUtilsService;
    }

    public void ensureFinancialCyclesExist(String procDate) {
        long cycleCount = finUtilsService.validateCycle(procDate);
        if (cycleCount == 0) {
            log.info("Creating financial cycles for date: {}", procDate);
            finUtilsService.prepareCycles(procDate);
        }
    }
}
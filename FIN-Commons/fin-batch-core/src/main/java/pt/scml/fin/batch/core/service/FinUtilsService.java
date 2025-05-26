package pt.scml.fin.batch.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.scml.fin.batch.core.exceptions.ConfigValueNotFoundForConfigNameException;
import pt.scml.fin.batch.core.exceptions.GameNotFoundException;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
import pt.scml.fin.model.dto.enums.ChannelEnum;
import pt.scml.fin.model.entities.FinDailyInvoice;
import pt.scml.fin.model.entities.FinGame;
import pt.scml.fin.model.repo.FinChannelRepository;
import pt.scml.fin.model.repo.FinConfigRepository;
import pt.scml.fin.model.repo.FinDailyInvoiceRepository;
import pt.scml.fin.model.repo.FinGameRepository;
import pt.scml.fin.model.repo.FinModuleRepository;
import pt.scml.fin.model.repo.FinPeriodRepository;

/**
 * Utility service for financial operations. Provides helper methods for financial modules,
 * configurations, and period validation.
 */
@Slf4j
@Service
public class FinUtilsService {

    private final FinPeriodRepository finPeriodRepository;
    private final FinModuleRepository finModuleRepository;
    private final FinConfigRepository finConfigRepository;
    private final FinGameRepository finGameRepository;
    private final FinChannelRepository finChannelRepository;
    private final FinDailyInvoiceRepository finDailyInvoiceRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public FinUtilsService(EntityManager entityManager, FinPeriodRepository finPeriodRepository,
        FinModuleRepository finModuleRepository, FinConfigRepository finConfigRepository,
        FinGameRepository finGameRepository, FinChannelRepository finChannelRepository,
        FinDailyInvoiceRepository finDailyInvoiceRepository) {
        this.entityManager = entityManager;
        this.finPeriodRepository = finPeriodRepository;
        this.finModuleRepository = finModuleRepository;
        this.finConfigRepository = finConfigRepository;
        this.finGameRepository = finGameRepository;
        this.finChannelRepository = finChannelRepository;
        this.finDailyInvoiceRepository = finDailyInvoiceRepository;
    }

    /**
     * Validates the number of financial cycles (periods) that contain the given execution date.
     *
     * @param executionDate the date to validate, in a compatible format (e.g., yyyy-MM-dd)
     * @return the count of financial periods containing the specified date
     */
    public long validateCycle(String executionDate) {
        log.info("Validating financial cycles for execution date: {}", executionDate);

        long cycleCount = this.finPeriodRepository.countPeriodsContainingDate(executionDate);

        log.info("Found {} financial cycles containing date: {}", cycleCount, executionDate);
        return cycleCount;
    }

    /**
     * Calls the stored procedure <code>P_PREPARECYCLES</code> to initialize or prepare financial
     * cycles based on a given execution date.
     *
     * @param executionDate the date used to prepare financial cycles
     */
    public void prepareCycles(String executionDate) {
        log.info("Preparing financial cycles for execution date: {}", executionDate);

        StoredProcedureQuery query = entityManager
            .createStoredProcedureQuery("P_PREPARECYCLES")
            .registerStoredProcedureParameter("s_date_input", String.class, ParameterMode.IN)
            .setParameter("s_date_input", executionDate);

        boolean executed = query.execute();
        log.info("Stored procedure P_PREPARECYCLES executed: {}, execution date: {}", executed,
            executionDate);
    }

    public String getConfigValue(String configName) {
        log.debug("Retrieving configuration value for: {}", configName);

        String configValueByConfigName = this.finConfigRepository.getConfigValueByConfigName(
            configName);
        if (!Strings.isEmpty(configValueByConfigName)) {
            log.debug("Found configuration value for {}: {}", configName, configValueByConfigName);
            return configValueByConfigName;
        }
        String errorMsg = String.format("Config value not found for config name: %s", configName);
        log.error(errorMsg);
        throw new ConfigValueNotFoundForConfigNameException(errorMsg);
    }

    public Long getFinModuleId(String shortDescription) {
        return this.finModuleRepository.findFinModuleIdByShortDescription(shortDescription);
    }

    public String getGameIdByGameShdesAndChannelId(String gameShdes, Long channelId) {
        Optional<FinGame> game = this.finGameRepository.findFirstByShortDescriptionAndIdChannelId(
            gameShdes, channelId);
        if (game.isEmpty()) {
            String errorMsg = String.format("Game not found. gameShdes = %s channelId = %d",
                gameShdes, channelId);
            throw new GameNotFoundException(errorMsg);
        }
        return game.get().getId().getGameId();
    }

    public Long getRegularChannel() {
        return this.finChannelRepository.findByShortDescription(ChannelEnum.REGULAR.getShdes())
            .getChannelId();
    }

    public int batchInsertFinDailyInvoice(List<FinDailyInvoiceDTO> records) {
        log.info("Batch inserting {} financial daily invoice records", records.size());

        List<FinDailyInvoice> entities = records.stream().map(FinDailyInvoiceDTO::toEntity)
            .toList();

        List<FinDailyInvoice> finDailyInvoices = this.finDailyInvoiceRepository.saveAll(entities);
        int insertedCount = finDailyInvoices.size();

        log.info("Successfully inserted {} financial daily invoice records", insertedCount);
        return insertedCount;
    }

    public void rollbackFinDailyInvoice(Long jobInstanceId) {
        log.info("Rollback FinDailyInvoice records with jobInstanceId {} ", jobInstanceId);
        this.finDailyInvoiceRepository.deleteAllByJobInstanceId(jobInstanceId);
        log.info("Successfully removed financial daily invoice records");
    }

}
package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.scml.fin.batch.core.exceptions.ConfigValueNotFoundForConfigNameException;
import pt.scml.fin.batch.core.exceptions.GameNotFoundException;
import pt.scml.fin.batch.core.service.FinUtilsService;
import pt.scml.fin.model.dto.FinDailyInvoiceDTO;
import pt.scml.fin.model.dto.enums.ChannelEnum;
import pt.scml.fin.model.entities.FinChannel;
import pt.scml.fin.model.entities.FinDailyInvoice;
import pt.scml.fin.model.entities.FinGame;
import pt.scml.fin.model.entities.FinGameId;
import pt.scml.fin.model.repo.FinChannelRepository;
import pt.scml.fin.model.repo.FinConfigRepository;
import pt.scml.fin.model.repo.FinDailyInvoiceRepository;
import pt.scml.fin.model.repo.FinGameRepository;
import pt.scml.fin.model.repo.FinModuleRepository;
import pt.scml.fin.model.repo.FinPeriodRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinUtilsServiceTest {

    @InjectMocks
    private FinUtilsService finUtilsService;

    @Mock
    private EntityManager entityManager;
    @Mock
    private FinPeriodRepository finPeriodRepository;
    @Mock
    private FinModuleRepository finModuleRepository;
    @Mock
    private FinConfigRepository finConfigRepository;
    @Mock
    private FinGameRepository finGameRepository;
    @Mock
    private FinChannelRepository finChannelRepository;
    @Mock
    private FinDailyInvoiceRepository finDailyInvoiceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateCycle_returnsCorrectCount() {
        when(finPeriodRepository.countPeriodsContainingDate("2024-05-01")).thenReturn(2L);

        long count = finUtilsService.validateCycle("2024-05-01");

        assertEquals(2L, count);
        verify(finPeriodRepository).countPeriodsContainingDate("2024-05-01");
    }

    @Test
    void testPrepareCycles_callsStoredProcedure() {
        StoredProcedureQuery mockQuery = mock(StoredProcedureQuery.class);
        when(entityManager.createStoredProcedureQuery("P_PREPARECYCLES")).thenReturn(mockQuery);
        when(mockQuery.registerStoredProcedureParameter(anyString(), any(), any())).thenReturn(
            mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.execute()).thenReturn(true);

        finUtilsService.prepareCycles("2024-05-01");

        verify(entityManager).createStoredProcedureQuery("P_PREPARECYCLES");
        verify(mockQuery).execute();
    }

    @Test
    void testGetConfigValue_returnsValue() {
        when(finConfigRepository.getConfigValueByConfigName("MAX_LIMIT")).thenReturn("500");

        String value = finUtilsService.getConfigValue("MAX_LIMIT");

        assertEquals("500", value);
    }

    @Test
    void testGetConfigValue_returnsDefaultWhenNotFound() {
        when(finConfigRepository.getConfigValueByConfigName("INVALID")).thenReturn(null);

        assertThrows(ConfigValueNotFoundForConfigNameException.class, () -> {
            finUtilsService.getConfigValue("INVALID");
        });
    }

    @Test
    void testGetFinModuleId_returnsValue() {
        when(finModuleRepository.findFinModuleIdByShortDescription("MOD1")).thenReturn(10L);

        Long id = finUtilsService.getFinModuleId("MOD1");

        assertEquals(10L, id);
    }

    @Test
    void testGetGameIdByGameShdesAndChannelId_returnsId() {
        FinGameId gameId = new FinGameId("11", 1L);
        FinGame game = new FinGame();
        game.setId(gameId);

        when(finGameRepository.findFirstByShortDescriptionAndIdChannelId("EM", 1L))
            .thenReturn(Optional.of(game));

        String result = finUtilsService.getGameIdByGameShdesAndChannelId("EM", 1L);

        assertEquals("11", result);
    }

    @Test
    void testGetGameIdByGameShdesAndChannelId_throwsException() {
        when(finGameRepository.findFirstByShortDescriptionAndIdChannelId("XPTO", 1L))
            .thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class, () ->
            finUtilsService.getGameIdByGameShdesAndChannelId("XPTO", 1L));
    }

    @Test
    void testGetRegularChannel_returnsChannelId() {
        FinChannel channel = new FinChannel();
        channel.setChannelId(1L);
        when(
            finChannelRepository.findByShortDescription(ChannelEnum.REGULAR.getShdes())).thenReturn(
            channel);

        Long result = finUtilsService.getRegularChannel();

        assertEquals(1L, result);
    }

    @Test
    void testBatchInsertFinDailyInvoice_returnsCount() {
        FinDailyInvoiceDTO dto = mock(FinDailyInvoiceDTO.class);
        FinDailyInvoice entity = new FinDailyInvoice();
        when(dto.toEntity()).thenReturn(entity);
        when(finDailyInvoiceRepository.saveAll(anyList())).thenReturn(List.of(entity));

        int result = finUtilsService.batchInsertFinDailyInvoice(List.of(dto));

        assertEquals(1, result);
    }

    @Test
    void testRollbackFinDailyInvoice_executesDelete() {
        doNothing().when(finDailyInvoiceRepository).deleteAllByJobInstanceId(42L);

        finUtilsService.rollbackFinDailyInvoice(42L);

        verify(finDailyInvoiceRepository).deleteAllByJobInstanceId(42L);
    }
}


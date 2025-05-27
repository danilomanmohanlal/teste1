package pt.scml.fin.job.li_itms.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.scml.fin.batch.core.context.ContextHeader;
import pt.scml.fin.batch.core.utils.DateUtils;
import pt.scml.fin.job.li_itms.JobParameters;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParameterValidatorTest {

    private ContextHeader contextHeader;
    private ParameterValidator validator;

    @BeforeEach
    void setUp() {
        contextHeader = mock(ContextHeader.class);
        validator = new ParameterValidator(contextHeader);
    }

    @Test
    void shouldSetDefaultDateIfProcDateIsEmpty() {
        JobParameters parameters = new JobParameters();
        parameters.setProcDate("");

        validator.validate(parameters);

        String expectedDate = DateUtils.getStringFromLocalDate(LocalDate.now().minusDays(1), DateUtils.YYYYMMDD);

        verify(contextHeader).setProcDate(expectedDate);
        verify(contextHeader).setFilename("INVITMS");
        verify(contextHeader).setModuleShdes("LIITMSINVOICING");
        verify(contextHeader).setJobHasAFile(true);

        assertThat(parameters.getProcDate()).isEqualTo(expectedDate);
    }

    @Test
    void shouldThrowExceptionWhenDateIsTodayOrAfter() {
        JobParameters parameters = new JobParameters();
        parameters.setProcDate(DateUtils.getStringFromLocalDate(LocalDate.now(), DateUtils.YYYYMMDD));

        assertThatThrownBy(() -> validator.validate(parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("must be before current date");
    }

    @Test
    void shouldAcceptValidPastDate() {
        String validDate = DateUtils.getStringFromLocalDate(LocalDate.now().minusDays(2), DateUtils.YYYYMMDD);
        JobParameters parameters = new JobParameters();
        parameters.setProcDate(validDate);

        validator.validate(parameters);

        verify(contextHeader).setProcDate(validDate);
        verify(contextHeader).setFilename("INVITMS");
        verify(contextHeader).setModuleShdes("LIITMSINVOICING");
        verify(contextHeader).setJobHasAFile(true);
    }
}

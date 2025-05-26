package pt.scml.fin.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.scml.fin.model.entities.ControlProcess;

@Repository
public interface ControlProcessRepository extends JpaRepository<ControlProcess, Long> {

    @Query(value =
        "SELECT COUNT(*) FROM FIN_CTRL_PROCESS p INNER JOIN FIN_CTRL_FILE f ON p.FIN_PROCESS_ID = f.FIN_PROCESS_ID"
            + " WHERE "
            + "p.FIN_MODULE_ID=:moduleId AND "
            + "p.PROCESS_STATUS=:processStatus AND "
            + "p.FUNCTIONAL_STATUS=:functionalStatus AND "
            + "f.DATA_START_DATE=to_date(:procDate, 'yyyymmdd')", nativeQuery = true)
    int countProcessedModule(@Param("moduleId") Long moduleId,
        @Param("processStatus") String processStatus,
        @Param("functionalStatus") String functionalStatus,
        @Param("procDate") String procDate);

    @Query(value = "SELECT * FROM FIN_CTRL_PROCESS p "
        + " WHERE "
        + "p.PROCESS_STATUS=:processStatus AND "
        + "p.FIN_MODULE_ID=:moduleId", nativeQuery = true)
    ControlProcess findProcessInExecutionByModuleId(@Param("processStatus") String processStatus,
        @Param("moduleId") Long moduleId);

}


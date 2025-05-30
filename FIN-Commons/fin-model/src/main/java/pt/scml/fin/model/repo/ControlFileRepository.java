package pt.scml.fin.model.repo;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.scml.fin.model.entities.ControlFile;

@Repository
public interface ControlFileRepository extends JpaRepository<ControlFile, Long> {


    @Query(value =
        "SELECT COUNT(*) FROM FIN_CTRL_PROCESS p INNER JOIN FIN_CTRL_FILE f ON p.FIN_PROCESS_ID = f.FIN_PROCESS_ID"
            + " WHERE "
            + "p.FIN_MODULE_ID=:moduleId AND "
            + "p.PROCESS_STATUS= 'S' AND "
            + "p.FUNCTIONAL_STATUS='S' AND "
            + "f.FILE_NAME=:filename  AND "
            + "f.STATUS='P'", nativeQuery = true)
    int countByFileNameAndControlProcessId(@Param("filename") String filename,
        @Param("moduleId") Long moduleId);

    boolean existsByFilenameAndStatus(String fileName, String status);

    @Query(value =
            "SELECT f.FIN_FILE_CTRL_ID FROM FIN_CTRL_FILE f"
                    + " WHERE "
                    + "f.FIN_PROCESS_ID=:processId", nativeQuery = true)
    List<Long> findByProcessIdControlProcessId(@Param("processId")Long processId);

}

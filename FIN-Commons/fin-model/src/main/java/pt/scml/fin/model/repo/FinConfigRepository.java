package pt.scml.fin.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.scml.fin.model.entities.FinConfig;

@Repository
public interface FinConfigRepository extends JpaRepository<FinConfig, Long> {

    @Query("SELECT fc.configValue FROM FinConfig fc WHERE fc.configName = :configName")
    String getConfigValueByConfigName(@Param("configName") String configName);

}

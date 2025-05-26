package pt.scml.fin.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.scml.fin.model.entities.FinModule;

@Repository
public interface FinModuleRepository extends JpaRepository<FinModule, Long> {

    @Query("SELECT fm.finModuleId FROM FinModule fm WHERE fm.shortDescription = :shortDescription")
    Long findFinModuleIdByShortDescription(@Param("shortDescription") String shortDescription);
}

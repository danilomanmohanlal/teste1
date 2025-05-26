package pt.scml.fin.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.scml.fin.model.entities.FinPeriodInv;

@Repository
public interface FinPeriodRepository extends JpaRepository<FinPeriodInv, Long> {

    /**
     * Counts financial periods that include the given execution date
     *
     * @param executeDate The date to check
     * @return Count of financial periods that include this date
     */
    @Query(
        "SELECT COUNT(p.finPeriodId) FROM FinPeriodInv p "
            + "WHERE to_date(:executeDate, 'yyyymmdd')  BETWEEN p.finBeginDate AND p.finEndDate")
    long countPeriodsContainingDate(@Param("executeDate") String executeDate);
}

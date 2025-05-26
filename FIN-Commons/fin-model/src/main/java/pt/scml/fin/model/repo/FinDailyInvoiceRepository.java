package pt.scml.fin.model.repo;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import pt.scml.fin.model.entities.FinDailyInvoice;

@Repository
public interface FinDailyInvoiceRepository extends JpaRepository<FinDailyInvoice, Long> {

    @Transactional
    @Modifying
    public void deleteAllByJobInstanceId(Long jobInstanceId);


}

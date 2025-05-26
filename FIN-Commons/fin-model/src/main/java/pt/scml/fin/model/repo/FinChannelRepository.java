package pt.scml.fin.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.scml.fin.model.entities.FinChannel;

public interface FinChannelRepository extends JpaRepository<FinChannel, Long> {

    FinChannel findByShortDescription(String shortDescription);

}

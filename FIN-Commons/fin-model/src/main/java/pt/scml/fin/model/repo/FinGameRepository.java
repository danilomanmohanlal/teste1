package pt.scml.fin.model.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.scml.fin.model.entities.FinGame;
import pt.scml.fin.model.entities.FinGameId;

public interface FinGameRepository extends JpaRepository<FinGame, FinGameId> {

    Optional<FinGame> findFirstByShortDescriptionAndIdChannelId(String shortDescription,
        Long channelId);

}

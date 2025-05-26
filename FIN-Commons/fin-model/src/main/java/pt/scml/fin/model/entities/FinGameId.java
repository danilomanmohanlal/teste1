package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Composite primary key class
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FinGameId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "GAME_ID", length = 2, nullable = false)
    private String gameId;

    @Column(name = "CHANNEL_ID", precision = 1, scale = 0, nullable = false)
    private Long channelId;
}

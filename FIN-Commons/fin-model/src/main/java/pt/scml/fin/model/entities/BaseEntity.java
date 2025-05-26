package pt.scml.fin.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder
public class BaseEntity {

    @Column(name = "ENTRY_DATE", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime entryDate;

    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastUpdateDate;

    @Column(name = "LAST_USER_ID", nullable = false)
    @LastModifiedBy
    private String lastUserId;

}

package vn.com.ssv.master_data.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.com.ssv.master_data.common.persistence.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "vault_count")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaultCount extends BaseEntity {
    @Column(name = "date")
    String date;

    @Column(name = "type", nullable = false, unique = true, updatable = false)
    String type;

    @Column(name = "count", nullable = false)
    Long count;
}

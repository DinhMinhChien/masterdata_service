package vn.com.ssv.master_data.feature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.com.ssv.master_data.common.persistence.BaseEntity;
import vn.com.ssv.master_data.common.response.PositionStatus;

@Entity
@Getter
@Setter
@Table(name = "positions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Position extends BaseEntity {
    @Column(name = "code", nullable = false, length = 50)
    String code;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", length = 500)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    PositionStatus status;
}

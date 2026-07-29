package vn.com.ssv.master_data.feature.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.com.ssv.master_data.common.persistence.BaseEntity;
import vn.com.ssv.master_data.common.response.OrganizationStatus;

@Getter
@Setter
@Entity
@Table(name = "organization")
public class Organization extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "address", length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrganizationStatus status;

    @Column(name = "description")
    private String description;
}
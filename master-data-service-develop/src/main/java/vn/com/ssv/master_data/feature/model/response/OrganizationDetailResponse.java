package vn.com.ssv.master_data.feature.model.response;

import lombok.*;
import vn.com.ssv.master_data.common.persistence.dto.BaseDto;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDetailResponse extends BaseDto {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String address;

    private String typeCode;

    private String typeName;

    private String parentCode;

    private String parentName;

    private String status;

    private String path;
}
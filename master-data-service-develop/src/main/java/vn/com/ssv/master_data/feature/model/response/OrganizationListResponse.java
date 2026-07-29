package vn.com.ssv.master_data.feature.model.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationListResponse {

    private Long id;

    private String code;

    private String name;

    private String typeCode;

    private String typeName;

    private String parentCode;

    private String parentName;

    private String status;
}
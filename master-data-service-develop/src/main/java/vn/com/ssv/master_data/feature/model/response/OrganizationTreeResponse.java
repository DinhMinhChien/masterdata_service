package vn.com.ssv.master_data.feature.model.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTreeResponse {

    private Long id;

    private String code;

    private String name;

    private String typeCode;

    private String typeName;

    private String status;

    @Builder.Default
    private List<OrganizationTreeResponse> children = new ArrayList<>();
}
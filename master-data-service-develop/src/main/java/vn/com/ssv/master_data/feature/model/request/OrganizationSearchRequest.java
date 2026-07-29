package vn.com.ssv.master_data.feature.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.ssv.master_data.common.persistence.dto.PageDto;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSearchRequest extends PageDto {

    private String keyword;
    private String typeCode;
    private String status;
    private String parentCode;
    private String sort;
    private String sortDirection;
}
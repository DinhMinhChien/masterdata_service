package vn.com.ssv.master_data.feature.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vn.com.ssv.master_data.feature.entity.Organization;
import vn.com.ssv.master_data.feature.model.request.OrganizationCreateRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationDetailResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationTreeResponse;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    Organization toEntity(OrganizationCreateRequest request);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            @MappingTarget Organization organization,
            OrganizationUpdateRequest request
    );

    OrganizationDetailResponse toDetailResponse(
            Organization organization
    );

    OrganizationListResponse toListResponse(
            Organization organization
    );

    OrganizationTreeResponse toTreeResponse(
            Organization organization
    );
}
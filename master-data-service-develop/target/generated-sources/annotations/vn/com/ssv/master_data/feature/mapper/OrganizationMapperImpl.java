package vn.com.ssv.master_data.feature.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.com.ssv.master_data.feature.entity.Organization;
import vn.com.ssv.master_data.feature.model.request.OrganizationCreateRequest;
import vn.com.ssv.master_data.feature.model.request.OrganizationUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.OrganizationDetailResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationListResponse;
import vn.com.ssv.master_data.feature.model.response.OrganizationTreeResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class OrganizationMapperImpl implements OrganizationMapper {

    @Override
    public Organization toEntity(OrganizationCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Organization organization = new Organization();

        organization.setCode( request.getCode() );
        organization.setName( request.getName() );
        organization.setAddress( request.getAddress() );
        organization.setStatus( request.getStatus() );
        organization.setDescription( request.getDescription() );

        return organization;
    }

    @Override
    public void updateEntity(Organization organization, OrganizationUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getCode() != null ) {
            organization.setCode( request.getCode() );
        }
        if ( request.getName() != null ) {
            organization.setName( request.getName() );
        }
        if ( request.getAddress() != null ) {
            organization.setAddress( request.getAddress() );
        }
        if ( request.getStatus() != null ) {
            organization.setStatus( request.getStatus() );
        }
        if ( request.getDescription() != null ) {
            organization.setDescription( request.getDescription() );
        }
    }

    @Override
    public OrganizationDetailResponse toDetailResponse(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationDetailResponse.OrganizationDetailResponseBuilder organizationDetailResponse = OrganizationDetailResponse.builder();

        organizationDetailResponse.id( organization.getId() );
        organizationDetailResponse.code( organization.getCode() );
        organizationDetailResponse.name( organization.getName() );
        organizationDetailResponse.description( organization.getDescription() );
        organizationDetailResponse.address( organization.getAddress() );
        if ( organization.getStatus() != null ) {
            organizationDetailResponse.status( organization.getStatus().name() );
        }
        organizationDetailResponse.path( organization.getPath() );

        return organizationDetailResponse.build();
    }

    @Override
    public OrganizationListResponse toListResponse(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationListResponse.OrganizationListResponseBuilder organizationListResponse = OrganizationListResponse.builder();

        organizationListResponse.id( organization.getId() );
        organizationListResponse.code( organization.getCode() );
        organizationListResponse.name( organization.getName() );
        if ( organization.getStatus() != null ) {
            organizationListResponse.status( organization.getStatus().name() );
        }

        return organizationListResponse.build();
    }

    @Override
    public OrganizationTreeResponse toTreeResponse(Organization organization) {
        if ( organization == null ) {
            return null;
        }

        OrganizationTreeResponse.OrganizationTreeResponseBuilder organizationTreeResponse = OrganizationTreeResponse.builder();

        organizationTreeResponse.id( organization.getId() );
        organizationTreeResponse.code( organization.getCode() );
        organizationTreeResponse.name( organization.getName() );
        if ( organization.getStatus() != null ) {
            organizationTreeResponse.status( organization.getStatus().name() );
        }

        return organizationTreeResponse.build();
    }
}

package vn.com.ssv.master_data.feature.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.com.ssv.master_data.feature.entity.Position;
import vn.com.ssv.master_data.feature.model.request.PositionCreateRequest;
import vn.com.ssv.master_data.feature.model.request.PositionUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.PositionDetailResponse;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class PositionMapperImpl implements PositionMapper {

    @Override
    public Position toEntity(PositionCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Position position = new Position();

        position.setCode( request.getCode() );
        position.setName( request.getName() );
        position.setDescription( request.getDescription() );
        position.setStatus( request.getStatus() );

        return position;
    }

    @Override
    public void updateEntity(Position position, PositionUpdateRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getCode() != null ) {
            position.setCode( request.getCode() );
        }
        if ( request.getName() != null ) {
            position.setName( request.getName() );
        }
        if ( request.getDescription() != null ) {
            position.setDescription( request.getDescription() );
        }
        if ( request.getStatus() != null ) {
            position.setStatus( request.getStatus() );
        }
    }

    @Override
    public PositionDetailResponse toDetailResponse(Position position) {
        if ( position == null ) {
            return null;
        }

        PositionDetailResponse.PositionDetailResponseBuilder positionDetailResponse = PositionDetailResponse.builder();

        positionDetailResponse.id( position.getId() );
        positionDetailResponse.code( position.getCode() );
        positionDetailResponse.name( position.getName() );
        positionDetailResponse.description( position.getDescription() );
        positionDetailResponse.status( position.getStatus() );

        return positionDetailResponse.build();
    }

    @Override
    public PositionListResponse toListResponse(Position position) {
        if ( position == null ) {
            return null;
        }

        PositionListResponse.PositionListResponseBuilder positionListResponse = PositionListResponse.builder();

        positionListResponse.id( position.getId() );
        positionListResponse.code( position.getCode() );
        positionListResponse.name( position.getName() );
        positionListResponse.description( position.getDescription() );
        positionListResponse.status( position.getStatus() );

        return positionListResponse.build();
    }
}

package vn.com.ssv.master_data.feature.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vn.com.ssv.master_data.feature.entity.Position;
import vn.com.ssv.master_data.feature.model.request.PositionCreateRequest;
import vn.com.ssv.master_data.feature.model.request.PositionUpdateRequest;
import vn.com.ssv.master_data.feature.model.response.PositionDetailResponse;
import vn.com.ssv.master_data.feature.model.response.PositionListResponse;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    Position toEntity(PositionCreateRequest request);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateEntity(
            @MappingTarget Position position,
            PositionUpdateRequest request
    );

    PositionDetailResponse toDetailResponse(Position position);

    PositionListResponse toListResponse(Position position);
}

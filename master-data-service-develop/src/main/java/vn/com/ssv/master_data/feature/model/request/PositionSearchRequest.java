package vn.com.ssv.master_data.feature.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.ssv.master_data.common.persistence.dto.PageDto;
import vn.com.ssv.master_data.common.response.PositionStatus;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PositionSearchRequest extends PageDto {
    private String keyword;
    private PositionStatus status;
}

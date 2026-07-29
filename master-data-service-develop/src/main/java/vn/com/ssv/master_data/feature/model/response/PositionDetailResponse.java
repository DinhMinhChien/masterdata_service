package vn.com.ssv.master_data.feature.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.ssv.master_data.common.persistence.dto.BaseDto;
import vn.com.ssv.master_data.common.response.PositionStatus;

@EqualsAndHashCode(callSuper = true)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDetailResponse extends BaseDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private PositionStatus status;
}

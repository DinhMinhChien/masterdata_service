package vn.com.ssv.master_data.feature.model.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import vn.com.ssv.master_data.common.persistence.dto.BaseDto;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationResponse extends BaseDto {

    Long id;
    String description;
}

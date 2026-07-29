package vn.com.ssv.master_data.feature.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateApplicationRequest {

    @NotBlank(message = "description không được trống")
    String description;
}

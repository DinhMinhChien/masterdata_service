package vn.com.ssv.master_data.feature.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.ssv.master_data.common.response.PositionStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionCreateRequest {
    @NotBlank(message = "Mã chức danh không được để trống")
    @Size(min = 2, max = 50, message = "Mã chức danh phải từ 2 đến 50 ký tự")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Mã chức danh phải bắt đầu bằng chữ cái in hoa và chỉ chứa chữ cái in hoa, số và dấu gạch dưới")
    private String code;

    @NotBlank(message = "Tên chức danh không được để trống")
    @Size(min = 2, max = 255, message = "Tên chức danh phải từ 2 đến 255 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả chức danh không được vượt quá 500 ký tự")
    private String description;

    @NotNull(message = "Trạng thái chức danh không được để trống")
    private PositionStatus status;
}

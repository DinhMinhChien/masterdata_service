package vn.com.ssv.master_data.feature.model.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.ssv.master_data.common.response.OrganizationStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUpdateRequest {
    @NotBlank(message=" Mã tổ chức không được để trống ")
    @Size(min=2,max = 50)
    @Pattern(regexp ="^[A-Z][A-Z0-9_]*$", message="Mã tổ chức chỉ được chứa chữ in hoa, số và dấu gạch dưới")
    private String code;
    @NotBlank(message="Tên tổ chức không được để trống")
    @Size(min=2, max=255)
    private String name;
    @Size(max=2000)
    private String description;
    @Size(max=500)
    private String address;

    @NotBlank(message = "Loại tổ chức không được để trống")
    private String typeCode;

    private String parentCode;
    @NotNull(message="Trạng thái không được để trống")
    private OrganizationStatus status;

}

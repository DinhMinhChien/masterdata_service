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
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCreateRequest {
     @NotBlank(message="Mã tổ chức không được để trống")
     @Size(min =2, max =50, message="Mã tổ chức phải từ 2 đến 50 ký tự")
     @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Mã tổ chức chỉ được chứa chữ in hoa, số và dấu gạch dưới")
     private String code;
     @NotBlank(message = "Tên tổ chức không được để trống")
     @Size(min = 2, max = 255, message = "Tên tổ chức phải từ 2 đến 255 ký tự")
     private String name;
     @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
     private String description;
     @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
     private String address;

     @NotBlank(message = "Loại tổ chức không được để trống")
     private String typeCode;

     private String parentCode;

     @NotNull(message = "Trạng thái không được để trống")
     private OrganizationStatus status;

}

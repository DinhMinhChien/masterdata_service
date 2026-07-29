package vn.com.ssv.master_data.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

//Danh mục mã phản hồi của service.
//Mỗi mã gồm: code (định danh), httpStatus (để handler/factory tự suy ra HTTP status)
//và message mặc định. {@code {}} trong message là chỗ thay tham số (ApiResponseFactory format).
//Quy ước code: {@code <PREFIX>-<NHÓM><SỐ>}. PREFIX đổi theo service khi clone
//Nhóm: 0xx=success, 4xx=lỗi client, 5xx=lỗi server/ngoài. Mã nghiệp vụ riêng thêm ở cuối enum.
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum DomainCode {

    //  Success 
    SUCCESS("SSV-000", HttpStatus.OK, "Thành công"),

    //  Lỗi client (4xx) 
    BAD_REQUEST("SSV-400", HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    VALIDATION_ERROR("SSV-401", HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ: {}"),
    INVALID_PARAMETER("SSV-402", HttpStatus.BAD_REQUEST, "Tham số không hợp lệ: {}"),
    UNAUTHORIZED("SSV-403", HttpStatus.UNAUTHORIZED, "Chưa xác thực"),
    TOKEN_EXPIRED("SSV-404", HttpStatus.UNAUTHORIZED, "Phiên đăng nhập đã hết hạn"),
    FORBIDDEN("SSV-405", HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này"),
    NOT_FOUND("SSV-406", HttpStatus.NOT_FOUND, "Không tìm thấy: {}"),
    CONFLICT("SSV-407", HttpStatus.CONFLICT, "Dữ liệu đã tồn tại hoặc đang xung đột: {}"),

    //  Lỗi server / service ngoài (5xx) 
    INTERNAL_ERROR("SSV-500", HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống, vui lòng thử lại sau"),
    EXTERNAL_SERVICE_ERROR("SSV-502", HttpStatus.BAD_GATEWAY, "Lỗi khi gọi dịch vụ ngoài: {}"),
    SERVICE_UNAVAILABLE("SSV-503", HttpStatus.SERVICE_UNAVAILABLE, "Dịch vụ tạm thời không khả dụng");

    //  Mã nghiệp vụ riêng (thêm theo service) 
    // Ví dụ: SAMPLE_NOT_FOUND("SSV-1001", HttpStatus.NOT_FOUND, "Không tìm thấy sample id {}"),


     String code;
     HttpStatus httpStatus;
     String message;
}

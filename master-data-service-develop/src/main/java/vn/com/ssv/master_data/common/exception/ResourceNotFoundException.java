package vn.com.ssv.master_data.common.exception;

import vn.com.ssv.master_data.common.response.DomainCode;

// Không tìm thấy tài nguyên -> 404
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(Object... args) {
        super(DomainCode.NOT_FOUND, args);
    }
}

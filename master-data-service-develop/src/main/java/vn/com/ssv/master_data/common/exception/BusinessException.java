package vn.com.ssv.master_data.common.exception;

import vn.com.ssv.master_data.common.response.DomainCode;

// Lỗi nghiệp vụ
public class BusinessException extends BaseException {

    public BusinessException(DomainCode domainCode, Object... args) {
        super(domainCode, args);
    }
}

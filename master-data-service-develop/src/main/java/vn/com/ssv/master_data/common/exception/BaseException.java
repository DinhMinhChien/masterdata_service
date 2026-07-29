package vn.com.ssv.master_data.common.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.slf4j.helpers.MessageFormatter;
import vn.com.ssv.master_data.common.response.DomainCode;

// Exception nền: gắn DomainCode + tham số format message
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public abstract class BaseException extends RuntimeException {

     DomainCode domainCode;
     transient Object[] args;

    protected BaseException(DomainCode domainCode, Object... args) {
        super(MessageFormatter.arrayFormat(domainCode.getMessage(), args).getMessage());
        this.domainCode = domainCode;
        this.args = args;
    }
}

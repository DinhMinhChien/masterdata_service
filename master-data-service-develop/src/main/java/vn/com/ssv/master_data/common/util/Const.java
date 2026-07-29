package vn.com.ssv.master_data.common.util;

import java.time.format.DateTimeFormatter;

public class Const {
    public static final String REALM_ACCESS = "realm_access";
    public static final String ADMIN = "ADMIN";
    public static final String ROLES = "roles";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String PREFERRED_USERNAME = "preferred_username";
    public static final String ANONYMOUS = "anonymous";
    public static final String REQUEST_ID = "requestId";
    public static final String USERNAME = "username";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final DateTimeFormatter FILE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static final int PAGE_DEFAULT = 0;
    public static final int SIZE_DEFAULT = 15;
    public static final int SIZE_MAX = 200;
}

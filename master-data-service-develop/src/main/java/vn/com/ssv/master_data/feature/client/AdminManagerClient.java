package vn.com.ssv.master_data.feature.client;

import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import vn.com.ssv.master_data.common.persistence.dto.RolePermissionDto;
import vn.com.ssv.master_data.common.response.ApiResponse;

import java.util.List;

@HttpExchange("${service.admin-service.context-path:admin}")
public interface AdminManagerClient {

    @GetExchange("${service.admin-service.endpoints.get-permission:/api/v1/role-permission/all}")
    ApiResponse<List<RolePermissionDto>> getAllRolePermissions();
}

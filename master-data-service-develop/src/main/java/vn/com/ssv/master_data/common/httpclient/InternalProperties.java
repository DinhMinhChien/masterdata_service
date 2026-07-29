package vn.com.ssv.master_data.common.httpclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.internal")
public class InternalProperties {

    private String baseUrl;
    private boolean enabled;
    private long refreshMs = 300000;
}

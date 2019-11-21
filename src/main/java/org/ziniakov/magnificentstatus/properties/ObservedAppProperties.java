package org.ziniakov.magnificentstatus.properties;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("observed-app")
public class ObservedAppProperties {

    private String url;

    private String name;

}

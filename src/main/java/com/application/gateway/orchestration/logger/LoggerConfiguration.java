package com.application.gateway.orchestration.logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoggerConfiguration implements ConfigurationBaseDTO {

    @JsonProperty("enable")
    private Boolean enable;

    @JsonProperty("all_path_match")
    private Boolean allPathMatch;

    @JsonProperty("custom_paths")
    private List<String> customPaths;

    @JsonProperty("threshold_min")
    private int thresholdMin;

    @JsonProperty("gw_task_subscriber")
    private Boolean gwTaskSubscriber = Boolean.FALSE;

    @Override
    public Boolean getEnable() {
        return enable;
    }
}

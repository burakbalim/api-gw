package com.application.gateway.main.policies.ratelimitter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RateLimit implements Serializable {

    private String key;

    private Integer rate;

    private Integer per;

    private Integer quotaMax;

    private Integer quotaRenewalRate;
}

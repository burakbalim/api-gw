package com.application.gateway.main.policies;

import com.application.gateway.main.policies.model.PolicyState;
import com.application.gateway.main.policies.model.Policies;
import com.application.gateway.main.policies.model.PoliciesCollections;
import com.application.gateway.main.policies.ratelimitter.RateLimit;
import com.application.gateway.main.policies.ratelimitter.RateLimiterService;
import com.application.gateway.orchestration.ConfigurableBase;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PoliciesProviderImpl extends ConfigurableBase<PoliciesCollections> implements PoliciesProvider {

    private ConfigurationSourceDTO<PoliciesCollections> configurationSourceDTO;
    private final RateLimiterService rateLimiterService;
    Map<String, PolicyState> policyStateMap;

    public PoliciesProviderImpl(ConfigurationProvider<PoliciesCollections> configurationProvider, RateLimiterService rateLimiterService) {
        super(configurationProvider);
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public void init(ConfigurationSourceDTO<PoliciesCollections> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        PoliciesCollections policiesCollections = getConfiguredFile(configurationSourceDTO);
        policiesCollections.stream().filter(policy -> policy.getState().equals(PolicyState.active)).forEach(policies -> this.rateLimiterService.configure(new RateLimit(policies.getName(), policies.getRate(), policies.getPer(), policies.getQuotaMax(), policies.getQuotaRenewalRate())));
        policiesCollections.stream().filter(policy -> policy.getState().equals(PolicyState.passive)).forEach(policies -> this.rateLimiterService.remove(policies.getName()));
        policyStateMap = policiesCollections.stream().collect(Collectors.toMap(Policies::getName, Policies::getState));
    }

    @Override
    public void apply(String policiesName) {
        PolicyState policyState = policyStateMap.get(policiesName);
        if (Objects.nonNull(policyState) && policyState.equals(PolicyState.active)) {
            this.rateLimiterService.tryConsume(policiesName);
        }
    }

    @Override
    protected void onNotifyConfigurationChange() {
        this.init(configurationSourceDTO);
    }
}

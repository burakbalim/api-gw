package com.application.gateway.orchestration;

import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.repository.ConfigurationProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract base class for orchestration services, providing common functionality for service initialization, starting, stopping, and processing.
 *
 * @param <T> The type of configuration data extending ConfigurationBaseDTO.
 * @param <K> The type of data to be processed by the orchestration service.
 */
@Slf4j
public abstract class OrchestrationBase<T extends ConfigurationBaseDTO, K> extends ConfigurableBase<T> implements OrchestrationService<T, K> {

    protected boolean isStopSignal = false;

    private T serviceConfigData;

    private ConfigurationSourceDTO<T> configurationSourceDTO;

    /**
     * Constructs an OrchestrationBase object with the provided configuration provider.
     *
     * @param configurationProvider The configuration provider used to obtain configuration data.
     */
    protected OrchestrationBase(ConfigurationProvider<T> configurationProvider) {
        super(configurationProvider);
    }

    /**
     * Method to be implemented by subclasses to define the processing logic for each piece of data.
     *
     * @param t The data to be processed.
     */
    protected abstract void onProcess(K t);

    /**
     * Method to be implemented by subclasses to perform initialization logic.
     *
     * @param configData The configuration data for the service.
     */
    protected abstract void onInit(T configData);

    @Override
    public void init(ConfigurationSourceDTO<T> configurationSourceDTO) {
        this.configurationSourceDTO = configurationSourceDTO;
        serviceConfigData = getConfiguredFile(configurationSourceDTO);
        onInit(serviceConfigData);
    }

    @Override
    public void start() {
        isStopSignal = Boolean.FALSE.equals(serviceConfigData.getEnable());
        log.info("Starting [service]: {} [stopSignal]= {} ", getClass().getName(), isStopSignal);
    }

    @Override
    public void process(K k) {
        if (!isStopSignal) {
            onProcess(k);
        }
    }

    @Override
    public void stop() {
        isStopSignal = true;
        log.info("Received stopping signal to [service]: {}", getClass().getName());
    }

    @Override
    public void onNotifyConfigurationChange() {
        stop();
        init(this.configurationSourceDTO);
        start();
    }
}
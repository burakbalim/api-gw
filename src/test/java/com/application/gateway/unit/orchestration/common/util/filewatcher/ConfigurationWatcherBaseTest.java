package com.application.gateway.unit.orchestration.common.util.filewatcher;

import com.application.gateway.orchestration.Configurable;
import com.application.gateway.orchestration.ConfigurationBaseDTO;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.orchestration.common.util.filewatcher.ConfigurationWatcherBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationWatcherBaseTest {

    @Mock
    private Configurable<TestConfig> configurable;

    private TestConfigWatcher configWatcher;

    private static class TestConfig implements ConfigurationBaseDTO {
        private Boolean enable = true;
        @Override
        public Boolean getEnable() {
            return enable;
        }
    }

    // Test implementation
    private static class TestConfigWatcher extends ConfigurationWatcherBase {
        private boolean initCalled = false;
        private boolean listenCalled = false;

        @Override
        protected void onInit() {
            initCalled = true;
        }

        @Override
        protected void onListen() {
            listenCalled = true;
        }

        public boolean isInitCalled() {
            return initCalled;
        }

        public boolean isListenCalled() {
            return listenCalled;
        }

        @Override
        protected void notifyToSubscriber(String configurationSourceName) {
            super.notifyToSubscriber(configurationSourceName);
        }
    }

    @BeforeEach
    void setUp() {
        configWatcher = new TestConfigWatcher();
        configWatcher.init();
    }

    @Test
    void start_ShouldCallOnListenAndInit() {
        // when
        configWatcher.start();

        // then
        assertTrue(configWatcher.isInitCalled());

        sleep();

        assertTrue(configWatcher.isListenCalled());
    }

    @Test
    void subscribe_ShouldAddToPathToServiceMap() {
        // given
        ConfigurationSourceDTO<TestConfig> sourceDTO = new ConfigurationSourceDTO<>();
        sourceDTO.setName("test-config");
        sourceDTO.setConfigurationSource("/path/to/config");
        sourceDTO.setSourceClazz(TestConfig.class);

        // when
        configWatcher.subscribe(sourceDTO, configurable);

        // then
        verify(configurable, never()).notifyConfigurationChange(); // subscription shouldn't trigger notification
    }

    @Test
    void notifyToSubscriber_ShouldNotifyConfigurable() {
        // given
        ConfigurationSourceDTO<TestConfig> sourceDTO = new ConfigurationSourceDTO<>();
        sourceDTO.setName("test-config");
        configWatcher.subscribe(sourceDTO, configurable);

        // when
        configWatcher.notifyToSubscriber("test-config");

        // then
        verify(configurable).notifyConfigurationChange();
    }

    @Test
    void notifyToSubscriber_WhenUnknownSubscriber_ShouldNotNotify() {
        // given
        ConfigurationSourceDTO<TestConfig> sourceDTO = new ConfigurationSourceDTO<>();
        sourceDTO.setName("test-config");
        configWatcher.subscribe(sourceDTO, configurable);

        // when
        configWatcher.notifyToSubscriber("unknown-config");

        // then
        verify(configurable, never()).notifyConfigurationChange();
    }

    @Test
    void subscribe_WhenCalledMultipleTimes_ShouldOverwriteExistingSubscription() {
        // given
        ConfigurationSourceDTO<TestConfig> sourceDTO = new ConfigurationSourceDTO<>();
        sourceDTO.setName("test-config");
        
        Configurable<TestConfig> configurable2 = mock(Configurable.class);

        // when
        configWatcher.subscribe(sourceDTO, configurable);
        configWatcher.subscribe(sourceDTO, configurable2);
        configWatcher.notifyToSubscriber("test-config");

        // then
        verify(configurable, never()).notifyConfigurationChange();
        verify(configurable2).notifyConfigurationChange();
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
} 
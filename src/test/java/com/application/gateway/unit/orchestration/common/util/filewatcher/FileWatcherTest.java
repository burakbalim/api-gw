package com.application.gateway.unit.orchestration.common.util.filewatcher;

import com.application.gateway.orchestration.Configurable;
import com.application.gateway.orchestration.common.dto.ConfigurationSourceDTO;
import com.application.gateway.testutil.orchestration.TestableFileWatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileWatcherTest {

    @Mock
    private Configurable<TestConfig> configurable;

    @TempDir
    Path tempDir;

    private TestableFileWatcher fileWatcher;
    private Thread watcherThread;
    private CountDownLatch startLatch;
    private CountDownLatch changeLatch;
    private AtomicBoolean running;

    @BeforeEach
    void setUp() {
        fileWatcher = new TestableFileWatcher();
        startLatch = new CountDownLatch(1);
        changeLatch = new CountDownLatch(1);
        running = new AtomicBoolean(true);
    }

    @Test
    void onInit_ShouldNotThrowException() {
        assertDoesNotThrow(() -> fileWatcher.publicOnInit());
    }

    @Test
    void listen_ShouldDetectFileChanges() throws IOException, InterruptedException {
        // given
        Path configFile = tempDir.resolve("test-config.json");
        Files.write(configFile, "{}".getBytes());

        ConfigurationSourceDTO<TestConfig> sourceDTO = new ConfigurationSourceDTO<>();
        sourceDTO.setName("test-config.json");
        sourceDTO.setConfigurationSource(configFile.toString());
        fileWatcher.subscribe(sourceDTO, configurable);
        fileWatcher.publicOnInit();

        // when
        startWatchThread();
        assertTrue(startLatch.await(5, TimeUnit.SECONDS), "Watcher thread didn't start");

        // Dosyayı değiştir
        Files.write(configFile, "{\"updated\":true}".getBytes());

        // Değişikliğin algılanması için bekle
        assertTrue(changeLatch.await(5, TimeUnit.SECONDS), "Change wasn't detected");

        // then
        verify(configurable, atLeastOnce()).notifyConfigurationChange();
    }

    @Test
    void listen_WhenMultipleSubscribers_ShouldNotifyAll() throws IOException, InterruptedException {
        // given
        Path configFile = tempDir.resolve("shared-config.json");
        Files.write(configFile, "{}".getBytes());

        Configurable<TestConfig> configurable2 = mock(Configurable.class);

        ConfigurationSourceDTO<TestConfig> sourceDTO = new ConfigurationSourceDTO<>();
        sourceDTO.setName("shared-config.json");
        sourceDTO.setConfigurationSource(configFile.toString());
        
        fileWatcher.subscribe(sourceDTO, configurable);
        fileWatcher.subscribe(sourceDTO, configurable2);
        fileWatcher.publicOnInit();

        // when
        startWatchThread();
        assertTrue(startLatch.await(5, TimeUnit.SECONDS), "Watcher thread didn't start");

        Files.write(configFile, "{\"updated\":true}".getBytes());
        assertTrue(changeLatch.await(5, TimeUnit.SECONDS), "Change wasn't detected");

        // then
        verify(configurable, atLeastOnce()).notifyConfigurationChange();
        verify(configurable2, atLeastOnce()).notifyConfigurationChange();
    }

    private void startWatchThread() {
        watcherThread = new Thread(() -> {
            try {
                startLatch.countDown(); // Thread başladığını bildir
                while (running.get()) {
                    fileWatcher.publicOnListen();
                    changeLatch.countDown(); // Değişiklik algılandığını bildir
                }
            } catch (Exception e) {
                if (!e.getMessage().contains("Interrupted")) {
                    throw e;
                }
            }
        });
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    @AfterEach
    void tearDown() {
        running.set(false);
        if (watcherThread != null && watcherThread.isAlive()) {
            watcherThread.interrupt();
            try {
                watcherThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
package com.application.gateway.testutil.orchestration;

import com.application.gateway.orchestration.common.util.filewatcher.FileWatcher;

public class TestableFileWatcher extends FileWatcher {
    
    public void publicOnInit() {
        super.onInit();
    }

    public void publicOnListen() {
        super.onListen();
    }
} 
package com.application.gateway.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class StreamUtils {

    private static final int BUFFER_SIZE = 1024;

    private StreamUtils() {

    }

    public static byte[] copyToByteArray(InputStream inputStream) {
        try {
            return read(inputStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] read(InputStream inputStream) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
}

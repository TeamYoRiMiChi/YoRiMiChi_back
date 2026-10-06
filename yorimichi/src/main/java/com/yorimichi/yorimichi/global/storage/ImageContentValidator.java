package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/** Decode every frame with bounded dimensions; MIME metadata alone is not trusted. */
final class ImageContentValidator {
    private static final long MAX_TOTAL_PIXELS = 40_000_000L;
    private static final int MAX_FRAMES = 200;
    private static final Map<String, String> FORMATS = Map.of(
            "image/jpeg", "JPEG", "image/png", "PNG",
            "image/gif", "GIF", "image/webp", "WEBP");

    private ImageContentValidator() { }

    static void validate(byte[] bytes, String contentType) {
        // GIF frame dimensions may be small even when its logical canvas is enormous.
        if ("image/gif".equals(contentType) && bytes.length >= 10) {
            int canvasWidth = (bytes[6] & 255) | ((bytes[7] & 255) << 8);
            int canvasHeight = (bytes[8] & 255) | ((bytes[9] & 255) << 8);
            if (canvasWidth <= 0 || canvasHeight <= 0
                    || (long) canvasWidth * canvasHeight > MAX_TOTAL_PIXELS) {
                throw invalid();
            }
        }
        try (var input = new MemoryCacheImageInputStream(new ByteArrayInputStream(bytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) {
                throw invalid();
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(input);
                if (!reader.getFormatName().toUpperCase(Locale.ROOT).equals(FORMATS.get(contentType))) {
                    throw invalid();
                }
                int frames = reader.getNumImages(true);
                if (frames <= 0 || frames > MAX_FRAMES) {
                    throw invalid();
                }
                long totalPixels = 0;
                for (int frame = 0; frame < frames; frame++) {
                    int width = reader.getWidth(frame);
                    int height = reader.getHeight(frame);
                    totalPixels += (long) width * height;
                    if (width <= 0 || height <= 0 || totalPixels > MAX_TOTAL_PIXELS) {
                        throw invalid();
                    }
                    var decoded = reader.read(frame);
                    if (decoded == null) {
                        throw invalid();
                    }
                    decoded.flush();
                }
            } finally {
                reader.dispose();
            }
        } catch (IOException | RuntimeException e) {
            throw invalid();
        }
    }

    private static CustomException invalid() {
        return new CustomException(ErrorCode.INVALID_IMAGE_FILE);
    }
}

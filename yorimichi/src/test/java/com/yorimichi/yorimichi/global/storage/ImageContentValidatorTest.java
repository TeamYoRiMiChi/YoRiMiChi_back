package com.yorimichi.yorimichi.global.storage;

import com.yorimichi.yorimichi.global.error.CustomException;
import org.junit.jupiter.api.Test;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import static org.assertj.core.api.Assertions.*;

class ImageContentValidatorTest {
    @Test
    void acceptsAnimatedGifButRejectsTooManyFrames() throws Exception {
        for (int frameCount : new int[]{2, 201}) {
            var output = new ByteArrayOutputStream();
            var writer = ImageIO.getImageWritersByFormatName("gif").next();
            try (var stream = ImageIO.createImageOutputStream(output)) {
                writer.setOutput(stream);
                writer.prepareWriteSequence(null);
                for (int frame = 0; frame < frameCount; frame++) {
                    writer.writeToSequence(new javax.imageio.IIOImage(
                            new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB), null, null), null);
                }
                writer.endWriteSequence();
            } finally {
                writer.dispose();
            }
            if (frameCount == 2) {
                assertThatCode(() -> ImageContentValidator.validate(output.toByteArray(), "image/gif"))
                        .doesNotThrowAnyException();
            } else {
                assertThatThrownBy(() -> ImageContentValidator.validate(output.toByteArray(), "image/gif"))
                        .isInstanceOf(CustomException.class);
            }
        }
    }

    @Test
    void acceptsRealLosslessWebp() {
        byte[] webp = Base64.getDecoder().decode("UklGRhwAAABXRUJQVlA4TA8AAAAvAUAAAAcQ/Y/+ByKi/wEA");
        assertThatCode(() -> ImageContentValidator.validate(webp, "image/webp"))
                .doesNotThrowAnyException();
    }

    @Test
    void acceptsDecodedJpegPngAndGif() throws Exception {
        for (String format : new String[]{"jpeg", "png", "gif"}) {
            var output = new ByteArrayOutputStream();
            ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), format, output);
            assertThatCode(() -> ImageContentValidator.validate(output.toByteArray(), "image/" + format))
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void rejectsActualFormatDifferentFromDeclaredMime() throws Exception {
        var output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB), "png", output);
        assertThatThrownBy(() -> ImageContentValidator.validate(output.toByteArray(), "image/gif"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void rejectsTruncatedImageAndExcessiveDimensions() {
        byte[] gif = Base64.getDecoder().decode("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");
        assertThatThrownBy(() -> ImageContentValidator.validate(java.util.Arrays.copyOf(gif, 10), "image/gif"))
                .isInstanceOf(CustomException.class);
        gif[6] = (byte) 0xff;
        gif[7] = (byte) 0xff;
        gif[8] = (byte) 0xff;
        gif[9] = (byte) 0xff;
        assertThatThrownBy(() -> ImageContentValidator.validate(gif, "image/gif"))
                .isInstanceOf(CustomException.class);
    }
}

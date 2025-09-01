package tryonu.api.fixture;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 테스트용 MultipartFile Mock Fixture
 */
public class MockFileFixture {

    public static MultipartFile createMockImageFile() {
        return new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }

    public static MultipartFile createMockImageFile(String filename) {
        return new MockMultipartFile(
                "file",
                filename,
                "image/jpeg",
                "test image content".getBytes());
    }

    public static MultipartFile createMockImageFile(String filename, String contentType) {
        return new MockMultipartFile(
                "file",
                filename,
                contentType,
                "test image content".getBytes());
    }

    public static MultipartFile createMockClothImageFile() {
        return new MockMultipartFile(
                "file",
                "test-cloth.jpg",
                "image/jpeg",
                "test cloth image content".getBytes());
    }

    public static MultipartFile createMockModelImageFile() {
        return new MockMultipartFile(
                "file",
                "test-model.jpg",
                "image/jpeg",
                "test model image content".getBytes());
    }

    public static MultipartFile createEmptyMockFile() {
        return new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]);
    }

    public static byte[] createMockImageBytes() {
        return "test image bytes content".getBytes();
    }
}

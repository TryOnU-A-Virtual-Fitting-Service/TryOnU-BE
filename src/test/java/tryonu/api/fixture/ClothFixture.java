package tryonu.api.fixture;

import tryonu.api.domain.Cloth;
import tryonu.api.common.enums.Category;

/**
 * 테스트용 Cloth 엔티티 Fixture
 */
public class ClothFixture {

    public static Cloth createCloth() {
        return Cloth.builder()
                .imageUrl("https://test-bucket.s3.amazonaws.com/clothes/test-shirt.jpg")
                .productPageUrl("https://test-fashion.com/products/test-shirt")
                .category(Category.LONG_SLEEVE)
                .build();
    }

    public static Cloth createCloth(String imageUrl, String productPageUrl, Category category) {
        return Cloth.builder()
                .imageUrl(imageUrl)
                .productPageUrl(productPageUrl)
                .category(category)
                .build();
    }

    public static Cloth createClothWithId(Long id, Category category) {
        Cloth cloth = createCloth(
                "https://test-bucket.s3.amazonaws.com/clothes/test-" + category.name().toLowerCase() + ".jpg",
                "https://test-fashion.com/products/test-" + category.name().toLowerCase(),
                category);
        try {
            var idField = Cloth.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(cloth, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
        return cloth;
    }

    public static Cloth createTopCloth() {
        return createCloth(
                "https://test-bucket.s3.amazonaws.com/clothes/test-top.jpg",
                "https://test-fashion.com/products/test-top",
                Category.LONG_SLEEVE);
    }

    public static Cloth createBottomCloth() {
        return createCloth(
                "https://test-bucket.s3.amazonaws.com/clothes/test-bottom.jpg",
                "https://test-fashion.com/products/test-bottom",
                Category.LONG_PANTS);
    }

    public static Cloth createDressCloth() {
        return createCloth(
                "https://test-bucket.s3.amazonaws.com/clothes/test-dress.jpg",
                "https://test-fashion.com/products/test-dress",
                Category.LONG_SLEEVE);
    }
}

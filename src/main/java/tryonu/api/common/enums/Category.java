package tryonu.api.common.enums;

import lombok.Getter;

@Getter
public enum Category {
    ACCESSORY(SuperType.ACCESSORY),
    LONG_PANTS(SuperType.BOTTOM),
    LONG_SLEEVE(SuperType.TOP),
    OUTWEAR(SuperType.TOP),
    SHOES(SuperType.FOOTWEAR),
    SHORT_PANTS(SuperType.BOTTOM),
    SHORT_SLEEVE(SuperType.TOP),
    SLEEVELESS(SuperType.TOP);


    private final SuperType superType;

    Category(SuperType superType) {
        this.superType = superType;
    }

    public SuperType getSuperType() {
        return superType; // SuperType.ACCESSORY, SuperType.BOTTOM, SuperType.TOP, SuperType.FOOTWEAR
    }

    public String getCategoryName() {
        return this.name(); // "ACCESSORY", "LONG_PANTS", "LONG_SLEEVE", "OUTWEAR", "SHOES", "SHORT_PANTS", "SHORT_SLEEVE", "SLEEVELESS"
    }

} 
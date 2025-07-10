package tryonu.api.common.enums;

public enum Category {
    TOP("상의"),
    BOTTOM("하의"),
    OUTER("아우터"),
    DRESS("원피스"),
    SHOES("신발"),
    BAG("가방"),
    ACCESSORY("액세서리");
    
    private final String description;
    
    Category(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getValue() {
        return this.name();
    }
    
    public static Category fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // 먼저 enum 상수명으로 시도
        try {
            return Category.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // enum 상수명이 아니면 한글 설명으로 시도
            for (Category category : Category.values()) {
                if (category.getDescription().equals(value)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Invalid category value: " + value);
        }
    }
    
    public static Category fromInteger(Integer value) {
        if (value == null) {
            return null;
        }
        switch (value) {
            case 0:
                return TOP;
            case 1:
                return BOTTOM;
            case 2:
                return OUTER;
            case 3:
                return DRESS;
            case 4:
                return SHOES;
            case 5:
                return BAG;
            case 6:
                return ACCESSORY;
            default:
                throw new IllegalArgumentException("Invalid category integer value: " + value);
        }
    }
    
    public Integer toInteger() {
        switch (this) {
            case TOP:
                return 0;
            case BOTTOM:
                return 1;
            case OUTER:
                return 2;
            case DRESS:
                return 3;
            case SHOES:
                return 4;
            case BAG:
                return 5;
            case ACCESSORY:
                return 6;
            default:
                throw new IllegalArgumentException("Unknown category: " + this);
        }
    }
} 
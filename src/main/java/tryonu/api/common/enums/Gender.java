package tryonu.api.common.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성");
    
    private final String description;
    
    Gender(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description; // "남성" 또는 "여성"
    }
    
    public String getValue() {
        return this.name(); //  "MALE" 또는 "FEMALE"
    }
    
    public static Gender fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // 먼저 enum 상수명으로 시도
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // enum 상수명이 아니면 한글 설명으로 시도
            for (Gender gender : Gender.values()) {
                if (gender.getDescription().equals(value)) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Invalid gender value: " + value);
        }
    }
    
    public static Gender fromInteger(Integer value) {
        if (value == null) {
            return null;
        }
        switch (value) {
            case 0:
                return MALE;
            case 1:
                return FEMALE;
            default:
                throw new IllegalArgumentException("Invalid gender integer value: " + value);
        }
    }
    
    public Integer toInteger() {
        switch (this) {
            case MALE:
                return 0;
            case FEMALE:
                return 1;
            default:
                throw new IllegalArgumentException("Unknown gender: " + this);
        }
    }
} 
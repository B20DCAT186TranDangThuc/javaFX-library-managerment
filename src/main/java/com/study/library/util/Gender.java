package com.study.library.util;

public enum Gender {
    MALE("Nam"),
    FEMALE("Nữ"),
    OTHER("Khác");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromDisplayName(String displayName) {
        for (Gender gender : Gender.values()) {
            if (gender.displayName.equalsIgnoreCase(displayName.trim())) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy Gender cho displayName: " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}

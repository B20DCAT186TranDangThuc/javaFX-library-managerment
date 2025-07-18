package com.study.library.util;

public enum Status {
    ACTIVE("Đang hoạt động"),
    INACTIVE("Ngừng hoạt động"),
    BANNED("Bị khóa");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

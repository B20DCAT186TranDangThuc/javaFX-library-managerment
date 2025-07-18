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
    public static Status fromDisplayName(String displayName) {
        for (Status status : Status.values()) {
            if (status.displayName.equalsIgnoreCase(displayName.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy Gender cho displayName: " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}

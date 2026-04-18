package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

/**
 * Fine Status Enum
 * NONE - No fine (returned on time)
 * UNPAID - Fine exists but not yet paid
 * PAID - Fine has been paid
 */
public enum FineStatus {
    NONE,
    PAID,
    UNPAID
}

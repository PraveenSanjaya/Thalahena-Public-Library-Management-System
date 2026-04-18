package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

/**
 * Book Condition Enum - Tracks the physical condition of returned books
 * 
 * OCP (Open/Closed Principle):
 * - Open for extension: Can add new conditions (e.g., EXCELLENT, DESTROYED)
 * - Closed for modification: Existing conditions won't change
 * 
 * Values:
 * - GOOD: No damage, book is in perfect condition
 * - POOR: Minor wear (e.g., slightly bent pages, minor stains)
 * - DAMAGED: Significant damage (e.g., torn pages, water damage, missing cover)
 */
public enum BookCondition {
    GOOD,
    POOR,
    DAMAGED
}

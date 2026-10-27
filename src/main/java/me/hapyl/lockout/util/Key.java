package me.hapyl.lockout.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Key implements Comparable<Key> {
    
    public static final Pattern PATTERN = Pattern.compile("^[a-z0-9_]+$");
    
    private final String key;
    
    private Key(@NotNull String key) throws IllegalArgumentException {
        if (!isValidString(key)) {
            throw new IllegalArgumentException("Key `%s` does not match the pattern `%s`!".formatted(key, PATTERN.pattern()));
        }
        
        this.key = key;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.key);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        final Key that = (Key) o;
        return Objects.equals(this.key, that.key);
    }
    
    @Override
    public String toString() {
        return key;
    }
    
    @Override
    public int compareTo(@NotNull Key that) {
        return this.key.compareTo(that.key);
    }
    
    public static @NotNull Key ofString(@NotNull String string) throws IllegalArgumentException {
        return new Key(string);
    }
    
    public static @Nullable Key ofStringOrNull(@NotNull String string) {
        try {
            return Key.ofString(string);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static @NotNull Key ofRandom() {
        final String uuid = UUID.randomUUID().toString();
        
        return Key.ofString(uuid.substring(0, uuid.indexOf("-")));
    }
    
    private static boolean isValidString(@NotNull String string) {
        return PATTERN.matcher(string).matches();
    }
    
}
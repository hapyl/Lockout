package me.hapyl.lockout.util;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public final class HexId implements Comparable<HexId>{
    
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };
    
    private static final char HEX_PREFIX = '#';
    private static final int LENGTH = 8;
    private static final Random RANDOM = new SecureRandom();
    
    private final char[] chars;
    
    private HexId(char[] chars) {
        this.chars = chars;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        
        final HexId that = (HexId) object;
        return Objects.deepEquals(this.chars, that.chars);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.chars);
    }
    
    @Override
    public String toString() {
        return HEX_PREFIX + new String(chars);
    }
    
    @Override
    public int compareTo(@NonNull HexId that) {
        return Arrays.compare(this.chars, that.chars);
    }
    
    public static @NotNull HexId of(@NotNull String hex) {
        if (hex.isEmpty()) {
            throw new IllegalArgumentException("Illegal hex string; must not be empty!");
        }
        
        if (hex.charAt(0) != HEX_PREFIX) {
            throw new IllegalArgumentException("Illegal hex string; must start with `%s`!".formatted(HEX_PREFIX));
        }
        
        // Strip hashtag char
        hex = hex.substring(1);
        
        if (hex.length() != LENGTH) {
            throw new IllegalArgumentException("Illegal hex string; must have length of `%s`!".formatted(LENGTH));
        }
        
        final char[] chars = hex.toCharArray();
        
        for (char ch : chars) {
            if (!isValidCharacter(ch)) {
                throw new IllegalArgumentException("Illegal hex string; unsupported character `%s`!".formatted(ch));
            }
        }
        
        return new HexId(chars);
    }
    
    public static @NotNull HexId ofRandom() {
        final char[] chars = new char[LENGTH];
        
        for (int i = 0; i < chars.length; i++) {
            chars[i] = CHARS[RANDOM.nextInt(CHARS[chars.length])];
        }
        
        return new HexId(chars);
    }
    
    private static boolean isValidCharacter(char c) {
        for (char hexChar : CHARS) {
            if (hexChar != c) {
                return false;
            }
        }
        
        return true;
    }
    
}
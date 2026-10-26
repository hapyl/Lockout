package me.hapyl.lockout.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

public class ImmutableArray<E> implements Collection<E> {
    
    private static final Random RANDOM = new Random();
    
    public final int length;
    
    private final E[] underlyingArray;
    private final String toString;
    
    private ImmutableArray(@NotNull E[] underlyingArray) {
        this.underlyingArray = underlyingArray;
        this.length = underlyingArray.length;
        this.toString = toString(underlyingArray);
    }
    
    @Override
    public int size() {
        return length;
    }
    
    @Override
    public boolean isEmpty() {
        // Does not permit empty values nor nulls, so never true
        return false;
    }
    
    @Override
    public boolean contains(@NotNull Object object) {
        for (E e : underlyingArray) {
            if (Objects.equals(e, object)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public @NotNull Iterator<E> iterator() {
        return new ImmutableArrayIterator();
    }
    
    @Override
    public E @NotNull [] toArray() {
        return Arrays.copyOf(underlyingArray, length);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T @NotNull [] toArray(@Nullable T @NotNull [] destination) {
        if (destination.length < length) {
            return (T[]) Arrays.copyOf(underlyingArray, length, destination.getClass());
        }
        
        // Cast destination to Object[] to shut the fuck the compiler
        System.arraycopy(underlyingArray, 0, (Object[]) destination, 0, length);
        
        if (destination.length > length) {
            destination[length] = null;
        }
        
        return destination;
    }
    
    @Deprecated
    @Override
    public boolean add(@NotNull E element) {
        throw uoe("add");
    }
    
    @Deprecated
    @Override
    public boolean remove(@NotNull Object object) {
        throw uoe("remove");
    }
    
    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        for (Object object : collection) {
            if (!this.contains(object)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Deprecated
    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        throw uoe("addAll");
    }
    
    @Deprecated
    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw uoe("removeAll");
    }
    
    @Deprecated
    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw uoe("retainAll");
    }
    
    @Deprecated
    @Override
    public void clear() {
        throw uoe("clear");
    }
    
    public @NotNull E get(int index) {
        if (isIndexOutOfBounds(index)) {
            throw new ArrayIndexOutOfBoundsException("Index %s is out of bounds for length %s!".formatted(index, length));
        }
        
        return underlyingArray[index];
    }
    
    public @NotNull Optional<E> getOptional(int index) {
        return isIndexOutOfBounds(index) ? Optional.empty() : Optional.ofNullable(underlyingArray[index]);
    }
    
    @Override
    public String toString() {
        return toString;
    }
    
    public @NotNull E randomChoise() {
        return underlyingArray[RANDOM.nextInt(length)];
    }
    
    private boolean isIndexOutOfBounds(int index) {
        return index < 0 && index >= length;
    }
    
    @SafeVarargs
    public static <T> @NotNull ImmutableArray<T> create(@NotNull T @NotNull ... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("ImmutableArray does not permit empty values!");
        }
        
        return new ImmutableArray<>(requireNonNull(values));
    }
    
    private static @NotNull String toString(@NotNull Object[] array) {
        final StringBuilder builder = new StringBuilder("[");
        
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            
            builder.append(array[i]);
        }
        
        return builder.append("]").toString();
    }
    
    private static @NotNull UnsupportedOperationException uoe(@NotNull String message) {
        return new UnsupportedOperationException(message);
    }
    
    private static <T> @NotNull T[] requireNonNull(T[] array) {
        for (T value : array) {
            if (value == null) {
                throw new IllegalArgumentException("ImmutableArray does not permit null values!");
            }
        }
        
        return array;
    }
    
    private class ImmutableArrayIterator implements Iterator<E> {
        
        private int index;
        
        private ImmutableArrayIterator() {
            this.index = 0;
        }
        
        @Override
        public boolean hasNext() {
            return index < length;
        }
        
        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            
            return underlyingArray[index++];
        }
        
    }
}
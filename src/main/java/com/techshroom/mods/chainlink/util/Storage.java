package com.techshroom.mods.chainlink.util;

import static com.techshroom.mods.chainlink.util.ChainUtil.cast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.google.auto.value.AutoValue;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;

/**
 * A put-only storage class. No removal!
 */
public class Storage {

    public interface Key<V> {

        @AutoValue
        abstract class Named<V> implements Key<V> {

            public static <V> Named<V> create(String name) {
                return new AutoValue_Storage_Key_Named<>(name);
            }

            public abstract String getName();

        }

        default V cast(Object o) {
            return ChainUtil.cast(o);
        }

    }

    public interface IntKey {

        @AutoValue
        abstract class Named implements IntKey {

            public static Named create(String name) {
                return new AutoValue_Storage_IntKey_Named(name);
            }

            public abstract String getName();

        }

    }

    public interface LongKey {

        @AutoValue
        abstract class Named implements LongKey {

            public static Named create(String name) {
                return new AutoValue_Storage_LongKey_Named(name);
            }

            public abstract String getName();

        }

    }

    public interface DoubleKey {

        @AutoValue
        abstract class Named implements DoubleKey {

            public static Named create(String name) {
                return new AutoValue_Storage_DoubleKey_Named(name);
            }

            public abstract String getName();

        }

    }

    public interface BooleanKey {

        @AutoValue
        abstract class Named implements BooleanKey {

            public static Named create(String name) {
                return new AutoValue_Storage_BooleanKey_Named(name);
            }

            public abstract String getName();

        }

    }

    private final Map<Key<?>, Object> map = new HashMap<>();
    private final TObjectIntMap<IntKey> intMap = new TObjectIntHashMap<>();
    private final TObjectLongMap<LongKey> longMap = new TObjectLongHashMap<>();
    private final TObjectDoubleMap<DoubleKey> doubleMap =
            new TObjectDoubleHashMap<>();
    private final Map<BooleanKey, Boolean> booleanMap = new HashMap<>();

    public Storage() { // heh
    }

    public <V> Optional<V> get(Key<V> key) {
        return Optional.ofNullable(key.cast(this.map.get(key)));
    }

    public <V> V getOrFail(Key<V> key) {
        return get(key).orElseThrow(() -> new IllegalStateException(
                "Expected key " + key + " to be present"));
    }

    public <V> V put(Key<V> key, V v) {
        Key<Object> kCast = cast(key);
        this.map.put(kCast, v);
        return v;
    }

    public OptionalInt get(IntKey key) {
        return this.intMap.containsKey(key)
                ? OptionalInt.of(this.intMap.get(key)) : OptionalInt.empty();
    }

    public int getOrFail(IntKey key) {
        return get(key).orElseThrow(() -> new IllegalStateException(
                "Expected key " + key + " to be present"));
    }

    public int put(IntKey key, int v) {
        return this.intMap.put(key, v);
    }

    public OptionalLong get(LongKey key) {
        return this.longMap.containsKey(key)
                ? OptionalLong.of(this.longMap.get(key)) : OptionalLong.empty();
    }

    public long getOrFail(LongKey key) {
        return get(key).orElseThrow(() -> new IllegalStateException(
                "Expected key " + key + " to be present"));
    }

    public long put(LongKey key, long v) {
        return this.longMap.put(key, v);
    }

    public OptionalDouble get(DoubleKey key) {
        return this.doubleMap.containsKey(key)
                ? OptionalDouble.of(this.doubleMap.get(key))
                : OptionalDouble.empty();
    }

    public double getOrFail(DoubleKey key) {
        return get(key).orElseThrow(() -> new IllegalStateException(
                "Expected key " + key + " to be present"));
    }

    public double put(DoubleKey key, double v) {
        return this.doubleMap.put(key, v);
    }

    public OptionalBoolean get(BooleanKey key) {
        return this.booleanMap.containsKey(key)
                ? OptionalBoolean.of(this.booleanMap.get(key) == Boolean.TRUE)
                : OptionalBoolean.empty();
    }

    public boolean getOrFail(BooleanKey key) {
        return get(key).orElseThrow(() -> new IllegalStateException(
                "Expected key " + key + " to be present"));
    }

    public boolean put(BooleanKey key, boolean v) {
        this.booleanMap.put(key, Boolean.valueOf(v));
        return v;
    }

    public boolean has(Key<?> key) {
        return this.map.containsKey(key);
    }

    public boolean has(IntKey key) {
        return this.intMap.containsKey(key);
    }

    public boolean has(LongKey key) {
        return this.longMap.containsKey(key);
    }

    public boolean has(DoubleKey key) {
        return this.doubleMap.containsKey(key);
    }

    public boolean has(BooleanKey key) {
        return this.booleanMap.containsKey(key);
    }

}

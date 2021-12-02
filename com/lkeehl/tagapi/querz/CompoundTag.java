package com.lkeehl.tagapi.querz;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CompoundTag extends Tag<Map<String, Tag<?>>> {

    public CompoundTag() {
        super(new HashMap<>(8));
    }

    public boolean doesNotContain(String key) {
        return !getValue().containsKey(key);
    }

    public Set<String> keySet() {
        return getValue().keySet();
    }

    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    public <C extends Tag<?>> C get(String key, Class<C> type) {
        Tag<?> t = getValue().get(key);
        if (t != null) {
            return type.cast(t);
        }
        return null;
    }

    public Tag<?> get(String key) {
        return getValue().get(key);
    }

    public CompoundTag getCompoundTag(String key) {
        return get(key, CompoundTag.class);
    }

    public int getInt(String key) {
        Tag<?> t = get(key);
        return t == null ? 0 : (int) t.getValue();
    }

    public void put(String key, Tag<?> tag) {
        getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(tag));
    }

}

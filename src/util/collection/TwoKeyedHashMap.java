package util.collection;

import util.Pair;

import java.util.*;

public class TwoKeyedHashMap<Key1, Key2, Value> extends TwoKeyedMap<Key1, Key2, Value> {
    private final Map<Key1, Map<Key2, Value>> mainMap = new HashMap<>();

    @Override
    public Value put(Key1 key1, Key2 key2, Value value) {
        return mainMap.computeIfAbsent(key1, sub -> new HashMap<>()).put(key2, value);
    }

    @Override
    public Value get(Key1 key1, Key2 key2) {
        Map<Key2, Value> subMap =  mainMap.get(key1);

        if(subMap != null) return subMap.get(key2);
        return null;
    }

    @Override
    public Value getOrDefault(Key1 key1, Key2 key2, Value defaultValue) {
        Value ret = this.get(key1, key2);

        if(ret == null) return defaultValue;
        return ret;
    }

    @Override
    public boolean containsKey(Key1 key1, Key2 key2) {
        if(mainMap.containsKey(key1))
            return mainMap.get(key1).containsKey(key2);

        return false;
    }

    @Override
    public Set<Pair<Key1, Key2>> keySet() {
        Set<Pair<Key1, Key2>> ret = new HashSet<>();

        for (Map.Entry<Key1, Map<Key2, Value>> entry : mainMap.entrySet())
            for (Key2 key2 : entry.getValue().keySet())
                ret.add(new Pair<>(entry.getKey(), key2));

        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoKeyedHashMap<?, ?, ?> that = (TwoKeyedHashMap<?, ?, ?>) o;
        return Objects.equals(mainMap, that.mainMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainMap);
    }
}

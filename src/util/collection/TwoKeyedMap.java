package util.collection;

import util.Pair;

import java.util.HashSet;
import java.util.Set;

public abstract class TwoKeyedMap<Key1, Key2, Value> {

    public abstract Value put(Key1 key1, Key2 key2, Value value);
    public abstract Value get(Key1 key1, Key2 key2);
    public abstract Value getOrDefault(Key1 key1, Key2 key2, Value defaultValue);
    public abstract boolean containsKey(Key1 key1, Key2 key2);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public abstract Set<Pair<Key1,Key2>> keySet();
    public Set<Value> valueSet() {
        Set<Value> ret = new HashSet<>();

        for(Pair<Key1, Key2> key : this.keySet()) {
            Key1 key1 = key.getKey();
            Key2 key2 = key.getValue();

            ret.add(this.get(key1, key2));
        }

        return ret;
    }
    public Set<Entry> entrySet() {
        Set<Entry> ret = new HashSet<>();

        for(Pair<Key1, Key2> key : this.keySet()) {
            Key1 key1 = key.getKey();
            Key2 key2 = key.getValue();

            ret.add(new TwoKeyedMap<Key1, Key2, Value>.Entry(key1, key2, this.get(key1, key2)));
        }

        return ret;
    }

    public class Entry {
        private final Key1 key1;
        private final Key2 key2;
        private final Value value;

        protected Entry(Key1 key1, Key2 key2, Value value) {
            this.key1 = key1;
            this.key2 = key2;
            this.value = value;
        }

        public Key1 getKey1() { return this.key1; }
        public Key2 getKey2() { return this.key2; }
        public Value getValue() { return this.value; }
        public Value setValue(Value value) { return put(this.key1, this.key2, value); }
    }
}

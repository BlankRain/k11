package mapindex;

import filedb.DataContainer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;

/**
 * 索引结构.仿hash-map实现.
 */
public class Index<T, R> {
    Map<Object, IndexNode> store = new HashMap<>();

    Function<T, Object> sf;
    Function<R, Object> tf;

    IndexNodeFactory factory;

    public Index(
            IndexNodeFactory factory,
            Function<T, Object> sourceFunc,
            Function<R, Object> targetFunc) {
        this.sf = sourceFunc;
        this.tf = targetFunc;
        this.factory = factory;
    }

    public void addSource(T obj) {
        Object k = sf.apply(obj);
        IndexNode v = store.getOrDefault(k, factory.makeIndexNode());
        if (v.source() == null) {
            v.source(obj);
        }
        store.put(k, v);
    }

    public void addTarget(R obj) throws Exception {
        Object k = tf.apply(obj);
        IndexNode v = store.getOrDefault(k, factory.makeIndexNode());
        v.target(obj);
        store.put(k, v);
    }

    @Override
    public String toString() {
        return store.toString();
    }
}

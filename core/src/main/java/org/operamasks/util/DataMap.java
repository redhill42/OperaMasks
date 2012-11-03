/*
 * $Id: DataMap.java,v 1.1 2008/04/19 11:50:28 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package org.operamasks.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DataMap <T,V> implements Map, Cloneable, Serializable 
{
    /**
     * 修改该类导致真实serialVersion与此不一致，请重新生成serialVersionUID值
     */
    private static final long serialVersionUID = -2603905422649090814L;

    static final int DEFAULT_INITIAL_CAPACITY = 10;

    static final float ALLOC_FACTOR = 1.5f;

    protected Object[] keys;

    protected Object[] values;

    protected int size;

    public DataMap(int initialCapacity) {
        keys = new Object[initialCapacity];
        values = new Object[initialCapacity];
        size = 0;
    }

    public DataMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public int size() {
        return size;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            keys[i] = null;
            values[i] = null;
        }
        size = 0;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        int position = searchKey(key);
        return position >= 0;
    }

    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return searchValue(value) >= 0;
    }

    public Collection values() {
        // Collection vs = valuesCollection;
        // return (vs != null ? vs : (valuesCollection = new Values()));
        return new Values();
    }

    public void putAll(Map t) {
        Iterator iterator = t.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) (iterator.next());
            this.put(key, t.get(key));
        }
    }

    public Set entrySet() {
        // Set es = entrySet;
        // return (es !=null ? es:(es = new EntrySet()));
        return new EntrySet();
    }

    public Set keySet() {
        // Set ks = keySet;
        // return (ks !=null ? ks:(ks = new KeySet()));
        return new KeySet();
    }

    public Object get(Object key) {
        int position = searchKey(key);
        if (position < 0) {
            return null;
        }

        assert position < size;
        return values[position];
    }

    public Object remove(Object key) {
        int position = searchKey(key);
        if (position < 0) {
            return null;
        }

        assert position < size;
        return internalRemove(position);
    }

    public Object put(Object key, Object value) {

        if (key == null) {
            throw new NullPointerException();
        }
        int position = searchKey(key);
        Object oldValue = null;

        Object newValue = value;

        if (value != null && value instanceof String) {
            newValue = ((String) (value)).intern();
        }

        if (position >= 0) {
            oldValue = values[position];
            values[position] = newValue;
        } else {
            internalInsert(position, ((String) key).intern(), newValue);
        }

        return oldValue;
    }

    public void trimToSize() {

        int oldCapacity = keys.length;
        if (size < oldCapacity) {
            Object oldKeys[] = keys;
            Object oldValues[] = values;
            keys = new Object[size];
            values = new Object[size];
            System.arraycopy(oldKeys, 0, keys, 0, size);
            System.arraycopy(oldValues, 0, values, 0, size);
        }

        // this.valuesCollection = null;
        // this.entrySet = null;
        // this.keySet = null;
    }

    /**
     * 二分法查找Key单元，返回所在位置或插入点
     * 
     * @param index
     * @return [int] 大于等于0为所在位置，否则为 -(插入点+1)
     */
    @SuppressWarnings("unchecked")
    private int searchKey(Object obj) {
        if (size == 0)
            return -1;

        int low = 0, high = size - 1;
        while (low <= high) {
            int mid = (low + high) >> 1;
            Object midVal = keys[mid];
            int cmp = ((Comparable) midVal).compareTo(obj);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    /**
     * 全局扫描Value,获取Value的位置
     */
    private int searchValue(Object obj) {
        if (size == 0)
            return -1;
        for (int i = 0; i < size; i++) {
            if (values[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 插入Kye-Value
     * 
     * @param pos
     *            插入位置
     * @param key
     * @param value
     */
    private void internalInsert(int pos, Object key, Object value) {
        if (isEmpty()) {
            if (keys.length == 0) {
                keys = new Object[1];
                values = new Object[1];
            }
            keys[0] = key;
            values[0] = value;
            size = 1;
        } else {
            if (pos < 0) {
                pos = -(pos + 1);
            }
            if (size + 1 > keys.length) {

                Object[] newKeys;
                Object[] newValues;

                if (keys.length < 2) {
                    newKeys = new Object[2];
                    newValues = new Object[2];
                } else {
                    newKeys = new Object[(int) (keys.length * ALLOC_FACTOR)];
                    newValues = new Object[(int) (keys.length * ALLOC_FACTOR)];
                }

                System.arraycopy(keys, 0, newKeys, 0, pos);
                System.arraycopy(keys, pos, newKeys, pos + 1, size - pos);
                keys = newKeys;
                System.arraycopy(values, 0, newValues, 0, pos);
                System.arraycopy(values, pos, newValues, pos + 1, size - pos);
                values = newValues;
            } else {
                System.arraycopy(keys, pos, keys, pos + 1, size - pos);
                System.arraycopy(values, pos, values, pos + 1, size - pos);
            }
            keys[pos] = key;
            values[pos] = value;
            size++;
        }
    }

    /**
     * 
     * @param pos
     * @return 如果有匹配的,返回移除的Value
     */
    private Object internalRemove(int pos) {
        if (pos < 0 || pos >= size) {
            return null;
        }

        Object obj = values[pos];
        // pos + 1至数组尾部（本次偏移的单元数）
        int remain = size - (pos + 1);
        System.arraycopy(keys, pos + 1, keys, pos, remain);
        System.arraycopy(values, pos + 1, values, pos, remain);
        // 将尾部的失效单元清空
        keys[size - 1] = null;
        values[size - 1] = null;
        size--;
        return obj;
    }

    protected Entry getEntry(Object k) {
        Object value = get(k);
        if (value != null) {
            return new Entry(k, value);
        } else {
            return null;
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");

        Iterator i = entrySet().iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
            Entry e = (Entry) (i.next());
            Object key = e.getKey();
            Object value = e.getValue();
            buf.append((key == this ? "(this Map)" : key) + "="
                    + (value == this ? "(this Map)" : value));

            hasNext = i.hasNext();
            if (hasNext)
                buf.append(", ");
        }

        buf.append("}");
        return buf.toString();
    }

    /**
     * Returns a shallow copy of this <tt>SortedStringObjectMap</tt> instance:
     * the keys and values themselves are not cloned.
     * 
     * @return a shallow copy of this map.
     */
    @SuppressWarnings("unchecked")
    public Object clone() {
        DataMap that = new DataMap(this.size);
        System.arraycopy(this.keys, 0, that.keys, 0, size);
        System.arraycopy(this.values, 0, that.values, 0, size);
        that.size = this.size;

        return that;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof Map))
            return false;
        Map t = (Map) obj;
        if (t.size() != size())
            return false;

        try {
            Iterator i = entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (!(t.get(key) == null && t.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(t.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        for (int i = 0; i < size; i++) {
            keys[i] = ((String) (keys[i])).intern();
            if (values[i] != null && values[i] instanceof String) {
                values[i] = ((String) (values[i])).intern();
            }
        }

        trimToSize();
    }

    private abstract class AbstractIterator implements Iterator {
        Entry next; // next entry to return

        int index; // current slot

        Entry current; // current entry

        AbstractIterator() {
            index = 0;
            Entry n = null;
            if (size != 0) {
                n = new Entry(keys[index], values[index]);
            }
            next = n;
        }

        public boolean hasNext() {
            return next != null;
        }

        Entry nextEntry() {
            Entry e = next;
            if (e == null)
                throw new NoSuchElementException();
            Entry n = null;
            if (index < size - 1) {
                n = new Entry(keys[index + 1], values[index + 1]);
            }
            next = n;
            index++;
            return current = e;
        }

        public void remove() {
            if (current == null)
                throw new IllegalStateException();
            Object k = current.key;
            current = null;
            DataMap.this.remove(k);
        }

    }

    private class EntryIterator extends AbstractIterator {
        /**
         * 覆盖方法
         * 
         * @see java.util.Iterator#next()
         */
        public Object next() {
            return nextEntry();
        }
    }

    private class KeyIterator extends AbstractIterator {
        public Object next() {
            return nextEntry().getKey();
        }
    }

    private class ValueIterator extends AbstractIterator {
        public Object next() {
            return nextEntry().value;
        }
    }

    @SuppressWarnings("unchecked")
    private class KeySet extends AbstractSet {
        public Iterator iterator() {
            return new KeyIterator();
        }

        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean remove(Object o) {
            return DataMap.this.remove(o) != null;
        }

        public void clear() {
            DataMap.this.clear();
        }
    }

    @SuppressWarnings("unchecked")
    private class EntrySet extends AbstractSet {
        public Iterator iterator() {
            return new EntryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry) o;
            Entry candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            else {
                Map.Entry entry = (Map.Entry) o;
                Object k = entry.getKey();
                return !(DataMap.this.remove(k) == null);
            }
        }

        public int size() {
            return size;
        }

        public void clear() {
            DataMap.this.clear();
        }
    }

    private class Values extends AbstractCollection {
        public Iterator iterator() {
            return new ValueIterator();
        }

        public int size() {
            return size;
        }

        public boolean contains(Object o) {
            return containsValue(o);
        }

        public void clear() {
            DataMap.this.clear();
        }
    }

    static class Entry implements Map.Entry {

        final Object key;

        Object value;

        Entry(Object k, Object v) {
            value = v;
            key = k;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object newValue) {
            Object oldValue = value;
            value = newValue;
            return oldValue;
        }

        public String toString() {
            return getKey() + "=" + getValue();
        }

    }

}

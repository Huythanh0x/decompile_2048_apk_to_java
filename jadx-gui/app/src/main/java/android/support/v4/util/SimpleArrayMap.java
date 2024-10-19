package android.support.v4.util;

import java.util.Map;

/* loaded from: classes.dex */
public class SimpleArrayMap<K, V> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    private static final String TAG = "ArrayMap";
    static Object[] mBaseCache;
    static int mBaseCacheSize;
    static Object[] mTwiceBaseCache;
    static int mTwiceBaseCacheSize;
    Object[] mArray;
    int[] mHashes;
    int mSize;

    int indexOf(Object obj, int i) {
        int i2 = this.mSize;
        if (i2 == 0) {
            return -1;
        }
        int binarySearch = ContainerHelpers.binarySearch(this.mHashes, i2, i);
        if (binarySearch < 0 || obj.equals(this.mArray[binarySearch << 1])) {
            return binarySearch;
        }
        int i3 = binarySearch + 1;
        while (i3 < i2 && this.mHashes[i3] == i) {
            if (obj.equals(this.mArray[i3 << 1])) {
                return i3;
            }
            i3++;
        }
        for (int i4 = binarySearch - 1; i4 >= 0 && this.mHashes[i4] == i; i4--) {
            if (obj.equals(this.mArray[i4 << 1])) {
                return i4;
            }
        }
        return i3 ^ (-1);
    }

    int indexOfNull() {
        int i = this.mSize;
        if (i == 0) {
            return -1;
        }
        int binarySearch = ContainerHelpers.binarySearch(this.mHashes, i, 0);
        if (binarySearch < 0 || this.mArray[binarySearch << 1] == null) {
            return binarySearch;
        }
        int i2 = binarySearch + 1;
        while (i2 < i && this.mHashes[i2] == 0) {
            if (this.mArray[i2 << 1] == null) {
                return i2;
            }
            i2++;
        }
        for (int i3 = binarySearch - 1; i3 >= 0 && this.mHashes[i3] == 0; i3--) {
            if (this.mArray[i3 << 1] == null) {
                return i3;
            }
        }
        return i2 ^ (-1);
    }

    private void allocArrays(int i) {
        if (i == 8) {
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCache != null) {
                    Object[] objArr = mTwiceBaseCache;
                    this.mArray = objArr;
                    mTwiceBaseCache = (Object[]) objArr[0];
                    this.mHashes = (int[]) objArr[1];
                    objArr[1] = null;
                    objArr[0] = null;
                    mTwiceBaseCacheSize--;
                    return;
                }
            }
        } else if (i == 4) {
            synchronized (ArrayMap.class) {
                if (mBaseCache != null) {
                    Object[] objArr2 = mBaseCache;
                    this.mArray = objArr2;
                    mBaseCache = (Object[]) objArr2[0];
                    this.mHashes = (int[]) objArr2[1];
                    objArr2[1] = null;
                    objArr2[0] = null;
                    mBaseCacheSize--;
                    return;
                }
            }
        }
        this.mHashes = new int[i];
        this.mArray = new Object[i << 1];
    }

    private static void freeArrays(int[] iArr, Object[] objArr, int i) {
        if (iArr.length == 8) {
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCacheSize < 10) {
                    objArr[0] = mTwiceBaseCache;
                    objArr[1] = iArr;
                    for (int i2 = (i << 1) - 1; i2 >= 2; i2--) {
                        objArr[i2] = null;
                    }
                    mTwiceBaseCache = objArr;
                    mTwiceBaseCacheSize++;
                }
            }
            return;
        }
        if (iArr.length == 4) {
            synchronized (ArrayMap.class) {
                if (mBaseCacheSize < 10) {
                    objArr[0] = mBaseCache;
                    objArr[1] = iArr;
                    for (int i3 = (i << 1) - 1; i3 >= 2; i3--) {
                        objArr[i3] = null;
                    }
                    mBaseCache = objArr;
                    mBaseCacheSize++;
                }
            }
        }
    }

    public SimpleArrayMap() {
        this.mHashes = ContainerHelpers.EMPTY_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
    }

    public SimpleArrayMap(int i) {
        if (i == 0) {
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        } else {
            allocArrays(i);
        }
        this.mSize = 0;
    }

    public SimpleArrayMap(SimpleArrayMap simpleArrayMap) {
        this();
        if (simpleArrayMap != null) {
            putAll(simpleArrayMap);
        }
    }

    public void clear() {
        if (this.mSize != 0) {
            freeArrays(this.mHashes, this.mArray, this.mSize);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
        }
    }

    public void ensureCapacity(int i) {
        if (this.mHashes.length < i) {
            int[] iArr = this.mHashes;
            Object[] objArr = this.mArray;
            allocArrays(i);
            if (this.mSize > 0) {
                System.arraycopy(iArr, 0, this.mHashes, 0, this.mSize);
                System.arraycopy(objArr, 0, this.mArray, 0, this.mSize << 1);
            }
            freeArrays(iArr, objArr, this.mSize);
        }
    }

    public boolean containsKey(Object obj) {
        return indexOfKey(obj) >= 0;
    }

    public int indexOfKey(Object obj) {
        return obj == null ? indexOfNull() : indexOf(obj, obj.hashCode());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int indexOfValue(Object obj) {
        int i = this.mSize * 2;
        Object[] objArr = this.mArray;
        if (obj == null) {
            for (int i2 = 1; i2 < i; i2 += 2) {
                if (objArr[i2] == null) {
                    return i2 >> 1;
                }
            }
            return -1;
        }
        for (int i3 = 1; i3 < i; i3 += 2) {
            if (obj.equals(objArr[i3])) {
                return i3 >> 1;
            }
        }
        return -1;
    }

    public boolean containsValue(Object obj) {
        return indexOfValue(obj) >= 0;
    }

    public V get(Object obj) {
        int indexOfKey = indexOfKey(obj);
        if (indexOfKey >= 0) {
            return (V) this.mArray[(indexOfKey << 1) + 1];
        }
        return null;
    }

    public K keyAt(int i) {
        return (K) this.mArray[i << 1];
    }

    public V valueAt(int i) {
        return (V) this.mArray[(i << 1) + 1];
    }

    public V setValueAt(int i, V v) {
        int i2 = (i << 1) + 1;
        V v2 = (V) this.mArray[i2];
        this.mArray[i2] = v;
        return v2;
    }

    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    public V put(K k, V v) {
        int i;
        int indexOf;
        if (k == null) {
            indexOf = indexOfNull();
            i = 0;
        } else {
            int hashCode = k.hashCode();
            i = hashCode;
            indexOf = indexOf(k, hashCode);
        }
        if (indexOf >= 0) {
            int i2 = (indexOf << 1) + 1;
            V v2 = (V) this.mArray[i2];
            this.mArray[i2] = v;
            return v2;
        }
        int i3 = indexOf ^ (-1);
        if (this.mSize >= this.mHashes.length) {
            int i4 = 4;
            if (this.mSize >= 8) {
                i4 = (this.mSize >> 1) + this.mSize;
            } else if (this.mSize >= 4) {
                i4 = 8;
            }
            int[] iArr = this.mHashes;
            Object[] objArr = this.mArray;
            allocArrays(i4);
            if (this.mHashes.length > 0) {
                System.arraycopy(iArr, 0, this.mHashes, 0, iArr.length);
                System.arraycopy(objArr, 0, this.mArray, 0, objArr.length);
            }
            freeArrays(iArr, objArr, this.mSize);
        }
        if (i3 < this.mSize) {
            int i5 = i3 + 1;
            System.arraycopy(this.mHashes, i3, this.mHashes, i5, this.mSize - i3);
            System.arraycopy(this.mArray, i3 << 1, this.mArray, i5 << 1, (this.mSize - i3) << 1);
        }
        this.mHashes[i3] = i;
        int i6 = i3 << 1;
        this.mArray[i6] = k;
        this.mArray[i6 + 1] = v;
        this.mSize++;
        return null;
    }

    public void putAll(SimpleArrayMap<? extends K, ? extends V> simpleArrayMap) {
        int i = simpleArrayMap.mSize;
        ensureCapacity(this.mSize + i);
        if (this.mSize != 0) {
            for (int i2 = 0; i2 < i; i2++) {
                put(simpleArrayMap.keyAt(i2), simpleArrayMap.valueAt(i2));
            }
        } else if (i > 0) {
            System.arraycopy(simpleArrayMap.mHashes, 0, this.mHashes, 0, i);
            System.arraycopy(simpleArrayMap.mArray, 0, this.mArray, 0, i << 1);
            this.mSize = i;
        }
    }

    public V remove(Object obj) {
        int indexOfKey = indexOfKey(obj);
        if (indexOfKey >= 0) {
            return removeAt(indexOfKey);
        }
        return null;
    }

    public V removeAt(int i) {
        int i2 = i << 1;
        V v = (V) this.mArray[i2 + 1];
        if (this.mSize <= 1) {
            freeArrays(this.mHashes, this.mArray, this.mSize);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
        } else {
            int i3 = 8;
            if (this.mHashes.length > 8 && this.mSize < this.mHashes.length / 3) {
                if (this.mSize > 8) {
                    i3 = (this.mSize >> 1) + this.mSize;
                }
                int[] iArr = this.mHashes;
                Object[] objArr = this.mArray;
                allocArrays(i3);
                this.mSize--;
                if (i > 0) {
                    System.arraycopy(iArr, 0, this.mHashes, 0, i);
                    System.arraycopy(objArr, 0, this.mArray, 0, i2);
                }
                if (i < this.mSize) {
                    int i4 = i + 1;
                    System.arraycopy(iArr, i4, this.mHashes, i, this.mSize - i);
                    System.arraycopy(objArr, i4 << 1, this.mArray, i2, (this.mSize - i) << 1);
                }
            } else {
                this.mSize--;
                if (i < this.mSize) {
                    int i5 = i + 1;
                    System.arraycopy(this.mHashes, i5, this.mHashes, i, this.mSize - i);
                    System.arraycopy(this.mArray, i5 << 1, this.mArray, i2, (this.mSize - i) << 1);
                }
                this.mArray[this.mSize << 1] = null;
                this.mArray[(this.mSize << 1) + 1] = null;
            }
        }
        return v;
    }

    public int size() {
        return this.mSize;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map map = (Map) obj;
        if (size() != map.size()) {
            return false;
        }
        for (int i = 0; i < this.mSize; i++) {
            try {
                K keyAt = keyAt(i);
                V valueAt = valueAt(i);
                Object obj2 = map.get(keyAt);
                if (valueAt == null) {
                    if (obj2 != null || !map.containsKey(keyAt)) {
                        return false;
                    }
                } else if (!valueAt.equals(obj2)) {
                    return false;
                }
            } catch (ClassCastException unused) {
                return false;
            } catch (NullPointerException unused2) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int[] iArr = this.mHashes;
        Object[] objArr = this.mArray;
        int i = this.mSize;
        int i2 = 0;
        int i3 = 1;
        int i4 = 0;
        while (i2 < i) {
            Object obj = objArr[i3];
            i4 += (obj == null ? 0 : obj.hashCode()) ^ iArr[i2];
            i2++;
            i3 += 2;
        }
        return i4;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            K keyAt = keyAt(i);
            if (keyAt != this) {
                sb.append(keyAt);
            } else {
                sb.append("(this Map)");
            }
            sb.append('=');
            V valueAt = valueAt(i);
            if (valueAt != this) {
                sb.append(valueAt);
            } else {
                sb.append("(this Map)");
            }
        }
        sb.append('}');
        return sb.toString();
    }
}

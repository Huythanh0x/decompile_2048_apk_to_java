package android.support.design.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.util.SparseArray;

/* loaded from: classes.dex */
public class ParcelableSparseArray extends SparseArray<Parcelable> implements Parcelable {
    public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<ParcelableSparseArray>() { // from class: android.support.design.internal.ParcelableSparseArray.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
        public ParcelableSparseArray createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new ParcelableSparseArray(parcel, classLoader);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.support.v4.os.ParcelableCompatCreatorCallbacks
        public ParcelableSparseArray[] newArray(int i) {
            return new ParcelableSparseArray[i];
        }
    });

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ParcelableSparseArray() {
    }

    public ParcelableSparseArray(Parcel parcel, ClassLoader classLoader) {
        int readInt = parcel.readInt();
        int[] iArr = new int[readInt];
        parcel.readIntArray(iArr);
        Parcelable[] readParcelableArray = parcel.readParcelableArray(classLoader);
        for (int i = 0; i < readInt; i++) {
            put(iArr[i], readParcelableArray[i]);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        int size = size();
        int[] iArr = new int[size];
        Parcelable[] parcelableArr = new Parcelable[size];
        for (int i2 = 0; i2 < size; i2++) {
            iArr[i2] = keyAt(i2);
            parcelableArr[i2] = valueAt(i2);
        }
        parcel.writeInt(size);
        parcel.writeIntArray(iArr);
        parcel.writeParcelableArray(parcelableArr, i);
    }
}

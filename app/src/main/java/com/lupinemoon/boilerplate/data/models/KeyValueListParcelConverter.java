package com.lupinemoon.boilerplate.data.models;

import org.parceler.ParcelConverter;
import org.parceler.Parcels;

import io.realm.RealmList;

@SuppressWarnings("unused, WeakerAccess")
public class KeyValueListParcelConverter implements ParcelConverter<RealmList<KeyValue>> {
    @Override
    public void toParcel(RealmList<KeyValue> input, android.os.Parcel parcel) {
        if (input == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(input.size());
            for (KeyValue item : input) {
                parcel.writeParcelable(Parcels.wrap(item), 0);
            }
        }
    }

    @Override
    public RealmList<KeyValue> fromParcel(android.os.Parcel parcel) {
        int size = parcel.readInt();
        if (size < 0) {
            return null;
        }
        RealmList<KeyValue> items = new RealmList<>();
        for (int i = 0; i < size; ++i) {
            items.add((KeyValue) Parcels.unwrap(parcel.readParcelable(KeyValue.class.getClassLoader())));
        }
        return items;
    }
}

package com.lupinemoon.boilerplate.data.storage.local;

import android.content.Context;

import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.data.models.AuthToken;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.data.models.NetworkHeader;
import com.lupinemoon.boilerplate.data.models.NetworkMediaType;
import com.lupinemoon.boilerplate.data.models.NetworkRequest;
import com.lupinemoon.boilerplate.data.models.NetworkRequestBody;
import com.lupinemoon.boilerplate.data.storage.interfaces.AppDataStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;

public class AppLocalDataStore implements AppDataStore {

    private static AppLocalDataStore appLocalDataStore = new AppLocalDataStore();

    private static final String KEY_VALUE_RANDOM_CLASS = "RandomKeyValue.class";

    private AppLocalDataStore() {
        // Empty Constructor
    }

    public static synchronized AppLocalDataStore getInstance() {
        if (appLocalDataStore == null) {
            appLocalDataStore = new AppLocalDataStore();
        }
        return appLocalDataStore;
    }

    public static RealmConfiguration getRealmConfiguration() {
        return new RealmConfiguration
                .Builder()
                .name(String.format(Locale.getDefault(), "%s_db.realm", BuildConfig.FLAVOR))
                .deleteRealmIfMigrationNeeded() // https://realm.io/docs/java/latest/#migrations
                .build();
    }

    private Realm getRealm() {
        Timber.d("Open Realm: Thread: %s", Thread.currentThread().getName());
        try {
            return Realm.getInstance(getRealmConfiguration());
        } catch (Exception e) {
            Timber.e(e, "getRealm() Failed");
        }
        return Realm.getDefaultInstance();
    }

    private void closeRealm(Realm realm) {
        Timber.d("Close Realm: Thread: %s", Thread.currentThread().getName());
        try {
            if (realm != null) {
                realm.close();
            }
        } catch (Exception e) {
            Timber.e(e, "closeRealm(Realm realm) Failed");
        }
    }

    public void clearRealmDatabase() {
        Timber.w("Clearing Realm...");
        // Clear relevant realm data before logout
        clearRandomKeyValues();
    }

    @Override
    public Flowable<AuthToken> doLogin(
            Context context, String msisdn, String password, String websiteId) {
        // Never done locally
        return null;
    }

    @Override
    public Completable performNotifyApiCall(Context context) {
        return Completable.complete();
    }

    @Override
    public Flowable<KeyValue> fetchKeyValue(Context context, String key) {
        Realm iRealm = getRealm();
        try {
            KeyValue keyValue = iRealm
                    .where(KeyValue.class)
                    .contains("dbClass", KEY_VALUE_RANDOM_CLASS)
                    .equalTo("key", key)
                    .findFirst();
            if (keyValue != null) {
                // Get detached object from realm
                KeyValue detachedKeyValue = iRealm.copyFromRealm(keyValue);
                Timber.d("fetchKeyValue: %s", detachedKeyValue);
                if (detachedKeyValue != null) {
                    return Flowable.just(detachedKeyValue);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
        return Flowable.just(new KeyValue());
    }

    @Override
    public Flowable<KeyValue> saveKeyValue(Context context, final KeyValue keyValue) {
        Realm iRealm = getRealm();
        Timber.d("saveKeyValue: %s", keyValue);
        keyValue.setDbClass(KEY_VALUE_RANDOM_CLASS);
        keyValue.compoundPrimaryKey();
        try {
            iRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(keyValue);
                }
            });
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
        return Flowable.just(keyValue);
    }

    private void clearRandomKeyValues() {
        Realm iRealm = getRealm();
        Timber.d("clearRandomKeyValues");
        try {
            final RealmResults<KeyValue> result = iRealm.where(KeyValue.class).contains(
                    "dbClass",
                    KEY_VALUE_RANDOM_CLASS).findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    public Flowable<File> getRealmDatabaseFile(File tempDirectory) {
        Realm iRealm = getRealm();
        // Get or create an "export.realm" file
        File exportRealmFile = new File(tempDirectory, "export.realm");
        try {
            // If "export.realm" already exists, delete it
            exportRealmFile.delete();

            // Copy the current realm database to "export.realm"
            iRealm.writeCopyTo(exportRealmFile);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
        return Flowable.just(exportRealmFile);
    }

    public Flowable<List<NetworkRequest>> getNetworkRequests() {
        Realm iRealm = getRealm();
        List<NetworkRequest> networkRequests = new ArrayList<>();
        try {
            RealmResults<NetworkRequest> result = iRealm.where(NetworkRequest.class).findAll();
            if (result != null) {
                // Get detached object from realm
                Collection<NetworkRequest> detachedRequests = iRealm.copyFromRealm(result);
                Timber.d("getNetworkRequests: %s", detachedRequests.size());
                networkRequests.addAll(detachedRequests);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
        return Flowable.just(networkRequests);
    }

    public void saveNetworkRequest(final NetworkRequest networkRequest){
        Realm iRealm = getRealm();
        Timber.d("%s: saveNetworkRequest %s", this.getClass().getSimpleName(), networkRequest);
        try{
            iRealm.executeTransaction(new Realm.Transaction(){
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(networkRequest);
                }
            });
        }catch (Exception e){
            Timber.e(e);
        }finally {
            closeRealm(iRealm);
        }
    }

    private void removeNetworkHeader(NetworkHeader networkHeader) {
        Realm iRealm = getRealm();
        Timber.d("removeNetworkHeader");
        try {
            final RealmResults<NetworkHeader> result = iRealm
                    .where(NetworkHeader.class)
                    .equalTo("primaryKey", networkHeader.getPrimaryKey())
                    .findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    private void clearNetworkHeader() {
        Realm iRealm = getRealm();
        try {
            final RealmResults<NetworkHeader> result = iRealm.where(NetworkHeader.class).findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    private void removeNetworkMediaType(NetworkMediaType networkMediaType) {
        Realm iRealm = getRealm();
        Timber.d("removeNetworkMediaType");
        try {
            final RealmResults<NetworkMediaType> result = iRealm
                    .where(NetworkMediaType.class)
                    .equalTo("primaryKey", networkMediaType.getPrimaryKey())
                    .findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    private void clearNetworkMediaType() {
        Realm iRealm = getRealm();
        try {
            final RealmResults<NetworkMediaType> result = iRealm.where(NetworkMediaType.class).findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    public void removeNetworkRequest(NetworkRequest networkRequest) {
        Realm iRealm = getRealm();
        Timber.d("removeNetworkRequest");
        try {
            final RealmResults<NetworkRequest> result = iRealm
                    .where(NetworkRequest.class)
                    .equalTo("primaryKey", networkRequest.getPrimaryKey())
                    .findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
            removeNetworkHeader(networkRequest.getNetworkHeader());
            removeNetworkMediaType(networkRequest.getNetworkRequestBody().getNetworkMediaType());
            removeNetworkRequestBody(networkRequest.getNetworkRequestBody());
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    private void clearNetworkRequest() {
        Realm iRealm = getRealm();
        try {
            final RealmResults<NetworkRequest> result = iRealm.where(NetworkRequest.class).findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    private void removeNetworkRequestBody(NetworkRequestBody networkRequestBody) {
        Realm iRealm = getRealm();
        Timber.d("removeNetworkRequestBody");
        try {
            final RealmResults<NetworkRequestBody> result = iRealm
                    .where(NetworkRequestBody.class)
                    .equalTo("primaryKey", networkRequestBody.getPrimaryKey())
                    .findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }

    private void clearNetworkRequestBody() {
        Realm iRealm = getRealm();
        try {
            final RealmResults<NetworkRequestBody> result = iRealm.where(NetworkRequestBody.class).findAll();
            if (result != null) {
                iRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        result.deleteAllFromRealm();
                    }
                });
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            closeRealm(iRealm);
        }
    }
}

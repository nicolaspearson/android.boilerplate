package com.lupinemoon.boilerplate.data.storage;

import android.content.Context;
import android.text.TextUtils;

import com.lupinemoon.boilerplate.BuildConfig;
import com.lupinemoon.boilerplate.MainApplication;
import com.lupinemoon.boilerplate.data.models.AuthToken;
import com.lupinemoon.boilerplate.data.models.KeyValue;
import com.lupinemoon.boilerplate.data.models.NetworkRequest;
import com.lupinemoon.boilerplate.data.network.rest.ServiceGenerator;
import com.lupinemoon.boilerplate.data.storage.interfaces.AppDataStore;
import com.lupinemoon.boilerplate.data.storage.local.AppLocalDataStore;
import com.lupinemoon.boilerplate.data.storage.remote.AppRemoteDataStore;
import com.lupinemoon.boilerplate.presentation.services.rxbus.RxBus;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.PostRequestFailedEvent;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.QueueProcessingComplete;
import com.lupinemoon.boilerplate.presentation.services.rxbus.events.QueueProcessingStarted;
import com.lupinemoon.boilerplate.presentation.utils.NetworkUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import timber.log.Timber;

public class AppRepository implements AppDataStore {

    private static AppRepository appRepository = new AppRepository();

    private AppLocalDataStore appLocalDataStore;
    private AppRemoteDataStore appRemoteDataStore;

    private boolean networkFinished = false;
    private boolean processingQueue;

    private final long queueInterval = 5000;

    private CompositeDisposable rxBusEvents;

    private AppRepository() {
        appLocalDataStore = AppLocalDataStore.getInstance();
        appRemoteDataStore = AppRemoteDataStore.getInstance(this);
        processingQueue = false;
        createRxBusSubscriptions();
    }

    public static synchronized AppRepository getInstance() {
        if (appRepository == null) {
            appRepository = new AppRepository();
        }
        return appRepository;
    }

    /**
     *  Receives the events from the RxBus
     *  Extracts the request properties and persists to database.
     *  The NetworkRequest.class is used to compose the data structure for query operations
     */
    private void createRxBusSubscriptions() {

        rxBusEvents = new CompositeDisposable();
        rxBusEvents.add(
                RxBus.getDefault().observeEvents(PostRequestFailedEvent.class)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<PostRequestFailedEvent>() {
                    @Override
                    public void accept(@NonNull PostRequestFailedEvent postRequestFailedEvent) {
                        Timber.w("POST Request Failed");
                        Request request = postRequestFailedEvent.getRequest();
                        NetworkRequest networkRequest = new NetworkRequest(
                                request.method(),
                                request.url().toString(),
                                request.headers(),
                                request.body()
                        );
                        appLocalDataStore.saveNetworkRequest(networkRequest);
                    }
                }));

    }

    public void onTerminate() {
        if (rxBusEvents != null) {
            rxBusEvents.clear();
            rxBusEvents = null;
        }
    }

    public Flowable<File> getRealmDatabaseFile(File tempDirectory) {
        return appLocalDataStore.getRealmDatabaseFile(tempDirectory);
    }

    public void retryNetworkRequests(final Context context) {
        if (!processingQueue) {
            processingQueue = true;
            appLocalDataStore.getNetworkRequests()
                    .concatMapIterable(new Function<List<NetworkRequest>, Iterable<NetworkRequest>>() {
                        @Override
                        public Iterable<NetworkRequest> apply(@NonNull List<NetworkRequest> networkRequests) throws Exception {
                            return networkRequests;
                        }
                    })
                    .concatMap(new Function<NetworkRequest, Publisher<NetworkRequest>>() {
                        @Override
                        public Publisher<NetworkRequest> apply(@NonNull NetworkRequest networkRequest) throws Exception {
                            return Flowable.just(networkRequest).delay(queueInterval, TimeUnit.MILLISECONDS);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<NetworkRequest>() {
                        @Override
                        public void accept(@NonNull final NetworkRequest networkRequest) throws Exception {
                            try {
                                Timber.d("%s: Retrying request queue.", this.getClass().getSimpleName());

                                // If the queue is empty on Complete is called and nothing is emitted.
                                if (networkRequest != null) {
                                    Timber.d("Queue Exists: Posting Queue Started Event");
                                    RxBus.getDefault().post(new QueueProcessingStarted());
                                }

                                Call call = ServiceGenerator.retryRequest(
                                        context,
                                        networkRequest.getMethod(),
                                        networkRequest.getUrl(),
                                        networkRequest.getNetworkHeader().getHeaders(),
                                        networkRequest.getNetworkRequestBody().getSourceRequestBody());
                                if (call != null) {
                                    call.enqueue(new Callback() {
                                        @Override
                                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                            // We can remove the request from the queue if it
                                            // was sent, we might not get a successful result
                                            // i.e. 400 on some requests if already true on API
                                            if (response != null && NetworkUtils.hasActiveNetworkConnection(context)) {
                                                Timber.d("%s: Retry queue %s item successfully called.", this.getClass().getSimpleName(), call);
                                                appLocalDataStore.removeNetworkRequest(networkRequest);
                                                try {
                                                    // This is used to perform post processing
                                                    // of certain API calls, e.g. sendMessage
                                                    if (response.isSuccessful()
                                                            && response.body() != null
                                                            && response.request() != null
                                                            && response.request().url() != null
                                                            && !TextUtils.isEmpty(response.request().url().toString())) {

                                                        // Empty
                                                        // Block to handle success
                                                        // if needed

                                                        String url = response.request().url().toString();
                                                    }
                                                } catch (Exception e) {
                                                    Timber.w(e, "Post Processing Request Failed");
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Timber.w(e, "Process Request Failed");
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Timber.w(e, "Process Request Failed");
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            Timber.w(throwable, "Process Request Failed");
                            RxBus.getDefault().post(new QueueProcessingComplete());
                            processingQueue = false;
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            Timber.d("Process Request Complete");
                            RxBus.getDefault().post(new QueueProcessingComplete());
                            processingQueue = false;
                        }
                    });
        }
    }


    // region Auth API
    public String getAuthString(Context context) {
        AuthToken authToken = (AuthToken) MainApplication.getStorage(context).getObject(com.lupinemoon.boilerplate.presentation.utils.Constants.KEY_AUTH_TOKEN, AuthToken.class);
        if (authToken != null) {
            return String.format("Bearer %s", authToken.getToken());
        } else {
            return com.lupinemoon.boilerplate.presentation.utils.Constants.KEY_NO_AUTH_TOKEN;
        }
    }

    public void clearAuthString(Context context) {
        appLocalDataStore.clearRealmDatabase();
        MainApplication.getStorage(context).del(com.lupinemoon.boilerplate.presentation.utils.Constants.KEY_AUTH_TOKEN);
    }

    @Override
    public Flowable<AuthToken> doLogin(
            Context context, String msisdn, String password, String websiteId) {
        // Never done locally
        return appRemoteDataStore.doLogin(context, msisdn, password, websiteId);
    }

    @Override
    public Completable performNotifyApiCall(Context context) {
        // Fire both calls simultaneously on separate threads, return
        // the first result, perform update when the network returns
        Completable localCompletable = appLocalDataStore.performNotifyApiCall(context).subscribeOn(Schedulers.io());
        Completable remoteCompletable = appRemoteDataStore.performNotifyApiCall(context).subscribeOn(Schedulers.io());
        return localCompletable.andThen(remoteCompletable);
    }

    @Override
    public Flowable<KeyValue> fetchKeyValue(final Context context, String key) {
        // Fire both calls simultaneously on separate threads, return
        // the first result, perform update when the network returns
        return Flowable.mergeDelayError(
                appLocalDataStore.fetchKeyValue(context, key).subscribeOn(Schedulers.io()),
                appRemoteDataStore.fetchKeyValue(context, key)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .doOnNext(new Consumer<KeyValue>() {
                            @Override
                            public void accept(KeyValue newKeyValue) {
                                networkFinished = true;
                                appLocalDataStore.saveKeyValue(context, newKeyValue);
                            }
                        }))
                .filter(new Predicate<KeyValue>() {
                    @Override
                    public boolean test(KeyValue newKeyValue) {
                        // Return true if this result is returned from the network
                        if (networkFinished) {
                            networkFinished = false;
                            return true;
                        }
                        // Only return offline data if the user is offline or offline first strategy is required
                        return (!NetworkUtils.hasActiveNetworkConnection(context) || BuildConfig.LOCAL_REPO_OFFLINE_FIRST) && newKeyValue != null;
                    }
                });
    }

    @Override
    public Flowable<KeyValue> saveKeyValue(final Context context, final KeyValue keyValue) {
        Timber.d("Saving Key value");
        // Fire both calls simultaneously on separate threads, return
        // the first result, perform update when the network returns
        return Flowable.mergeDelayError(
                appLocalDataStore.saveKeyValue(context, keyValue).subscribeOn(Schedulers.io()),
                appRemoteDataStore.saveKeyValue(context, keyValue)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Consumer<KeyValue>() {
                            @Override
                            public void accept(KeyValue newKeyValue) {
                                networkFinished = true;
                            }
                        }))
                .filter(new Predicate<KeyValue>() {
                    @Override
                    public boolean test(KeyValue newKeyValue) {
                        // Return true if this result is returned from the network
                        if (networkFinished) {
                            networkFinished = false;
                            return true;
                        }
                        // Only return offline data if the user is offline or offline first strategy is required
                        return (!NetworkUtils.hasActiveNetworkConnection(context) || BuildConfig.LOCAL_REPO_OFFLINE_FIRST) && newKeyValue != null;
                    }
                });
    }
}

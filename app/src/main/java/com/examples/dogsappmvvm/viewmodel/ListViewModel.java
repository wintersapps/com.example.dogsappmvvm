package com.examples.dogsappmvvm.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.examples.dogsappmvvm.model.DogBreed;
import com.examples.dogsappmvvm.model.DogDao;
import com.examples.dogsappmvvm.model.DogDatabase;
import com.examples.dogsappmvvm.model.DogsApiService;
import com.examples.dogsappmvvm.util.NotificationsHelper;
import com.examples.dogsappmvvm.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends AndroidViewModel {

    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<>();
    public MutableLiveData<Boolean> dogsLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private DogsApiService dogsApiService = new DogsApiService();
    private CompositeDisposable disposable = new CompositeDisposable();

    private AsyncTask<List<DogBreed>, Void, List<DogBreed>> insertTask;
    private AsyncTask<Void, Void, List<DogBreed>> retrievedTask;

    private SharedPreferencesHelper prefHelper = SharedPreferencesHelper.getInstance(getApplication());
    private long refreshTime = 5 * 60 * 1000 * 1000 * 1000L;

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh(){
        checkCacheDuration();
        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        if(updateTime != 0 && currentTime - updateTime < refreshTime){
            fetchFromDatabase();
        }else {
            fetchFromRemote();
        }
    }

    public void refreshBypassCache(){
        fetchFromRemote();
    }

    private void checkCacheDuration(){
        String cachePreference = prefHelper.getCacheDuration();

        if(!cachePreference.equals("")){
            try {
                int cachePreferenceInt = Integer.parseInt(cachePreference);
                refreshTime = cachePreferenceInt * 1000 * 1000 * 1000L;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
    }

    private void fetchFromDatabase(){
        loading.setValue(true);
        retrievedTask = new RetrievedDogsTask();
        retrievedTask.execute();
    }

    private void fetchFromRemote() {
        loading.setValue(true);
        disposable.add(
                dogsApiService.getDogs()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                            @Override
                            public void onSuccess(List<DogBreed> dogBreeds) {
                                insertTask = new InsertDogsTask();
                                insertTask.execute(dogBreeds);
                                Toast.makeText(getApplication(), "Dogs retrieved from endpoint", Toast.LENGTH_SHORT).show();
                                NotificationsHelper.getInstance(getApplication()).createNotification();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dogsLoadError.setValue(true);
                                loading.setValue(false);
                                e.printStackTrace();
                            }
                        })
        );
    }

    private void dogsRetrieved(List<DogBreed> dogList){
        dogs.setValue(dogList);
        dogsLoadError.setValue(false);
        loading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
        if(insertTask != null){
            insertTask.cancel(true);
            insertTask = null;
        }
        if(retrievedTask != null){
            retrievedTask.cancel(true);
            retrievedTask = null;
        }
    }

    private class InsertDogsTask extends AsyncTask<List<DogBreed>, Void, List<DogBreed>>{

        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {
            List<DogBreed> list = lists[0];
            DogDao dao = DogDatabase.getInstance(getApplication()).dogDao();
            dao.deleteAllDogs();

            ArrayList<DogBreed> newList = new ArrayList<>(list);
            List<Long> result = dao.insertAll(newList.toArray(new DogBreed[0]));

            int i = 0;
            while(i < list.size()){
                list.get(i).setUid(result.get(i).intValue());
                ++i;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            prefHelper.saveUpdateTime(System.nanoTime());
        }
    }

    private class RetrievedDogsTask extends AsyncTask<Void, Void, List<DogBreed>>{

        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabase.getInstance(getApplication()).dogDao().getAllDogs();
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show();
        }
    }
}

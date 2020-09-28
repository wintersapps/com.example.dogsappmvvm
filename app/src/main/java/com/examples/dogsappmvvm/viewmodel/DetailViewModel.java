package com.examples.dogsappmvvm.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.examples.dogsappmvvm.model.DogBreed;
import com.examples.dogsappmvvm.model.DogDatabase;

public class DetailViewModel extends AndroidViewModel {

    public MutableLiveData<DogBreed> dogLiveData = new MutableLiveData<>();
    private RetrievedDogTask task;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetch(int uid){
        task = new RetrievedDogTask();
        task.execute(uid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(task != null){
            task.cancel(true);
            task = null;
        }
    }

    private class RetrievedDogTask extends AsyncTask<Integer ,Void, DogBreed>{

        @Override
        protected DogBreed doInBackground(Integer... integers) {
            int uid = integers[0];
            return DogDatabase.getInstance(getApplication()).dogDao().getDog(uid);
        }

        @Override
        protected void onPostExecute(DogBreed dogBreed) {
            dogLiveData.setValue(dogBreed);
        }
    }
}

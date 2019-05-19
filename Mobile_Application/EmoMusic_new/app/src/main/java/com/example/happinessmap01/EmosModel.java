package com.example.happinessmap01;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;


import com.example.happinessmap01.Database.Emos;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EmosModel extends AndroidViewModel {

    private EmosRepository mRepository;

    private List<Emos> emotions;

    public EmosModel (Application application) {
        super(application);
        mRepository = new EmosRepository(application);
        emotions = mRepository.getEmos();
    }

    List<Emos> getEmos() {
        return emotions;
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void insert(Emos emos) {
        mRepository.insert(emos);
    }

    public void insertAll(List<Emos> emos) {
        mRepository.insertAll(emos.toArray(new Emos[emos.size()]));
    }
}

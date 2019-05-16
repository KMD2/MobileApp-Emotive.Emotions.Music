package com.example.happinessmap01;

import android.app.Application;
import android.os.AsyncTask;

import com.example.happinessmap01.Database.AppDatabase;
import com.example.happinessmap01.Database.Emos;
import com.example.happinessmap01.Database.EmosDao;

import java.util.List;

public class EmosRepository {


    private EmosDao emosDao;
    private List<Emos> emotions;

    public EmosRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        emosDao = db.emosDao();
        emotions = emosDao.getAll();
    }

    List<Emos> getEmos() {
        return emotions;
    }


    public void insert(Emos emos) {
        new insertAsyncTask(emosDao).execute(emos);
    }

    public void insertAll(Emos... emos) {

        new insertAsyncTask(emosDao).execute(emos);
    }

    public void deleteAll() {
        new deleteAsyncTask(emosDao).execute();
    }

    public void fetchDateByDuration(Emos emos) { new insertAsyncTask(emosDao).execute(emos);
    }

    public void fetchInterestByDuration(Emos emos) {
        new insertAsyncTask(emosDao).execute(emos);
    }

    private static class insertAsyncTask extends AsyncTask<Emos, Void, Void> {

        private EmosDao mAsyncTaskDao;

        insertAsyncTask(EmosDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Emos... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class insertAllAsyncTask extends AsyncTask<Emos, Void, Void> {

        private EmosDao mAsyncTaskDao;

        insertAllAsyncTask(EmosDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Emos... params) {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private EmosDao mAsyncTaskDao;

        deleteAsyncTask(EmosDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
/*
    private static class fetchDateAsyncTask extends AsyncTask<Emos, Void, Void> {

        private EmosDao mAsyncTaskDao;

        fetchDateAsyncTask(EmosDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Emos... params) {
            mAsyncTaskDao.fetchDateByDuration(params[0].toString());
            return null;
        }
    }

    private static class fetchInterestAsyncTask extends AsyncTask<Emos, Void, Void> {

        private EmosDao mAsyncTaskDao;

        fetchInterestAsyncTask(EmosDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Emos... params) {
            mAsyncTaskDao.fetchInterestByDuration(params[0].toString());
            return null;
        }

    }
   */
}

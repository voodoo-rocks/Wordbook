package my.beelzik.mobile.wordbook.db;

import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Andrey on 03.01.2016.
 */
public class AsyncWordDBWriter {


    ExecutorService mExecutorService;
    WordDB.WordDBObservable mWordDBObservable;

    public AsyncWordDBWriter(WordDB.WordDBObservable wordDBObservable) {
        mExecutorService = Executors.newSingleThreadExecutor();
        mWordDBObservable = wordDBObservable;
    }


    public void insetRow(InsertAction insertAction,InsertCallback callback){

        InsertTask task = new InsertTask(insertAction,callback);
        task.executeOnExecutor(mExecutorService);
    }


    public void updateDelete(UpdateDeleteAction updateDeleteAction,UpdateDeleteCallback callback){
        UpdateDeleteTask task = new UpdateDeleteTask(updateDeleteAction,callback);
        task.executeOnExecutor(mExecutorService);
    }


    public interface InsertAction{
        Long insert();
    }


    public interface InsertCallback{

        void insertSuccess(Long id);

        void insertFailed();
    }




    private class InsertTask extends AsyncTask<Void,Void,Long>{

        InsertAction mAction;
        InsertCallback mInsertCallback;

        public InsertTask(InsertAction action, InsertCallback insertCallback) {
            mAction = action;
            mInsertCallback = insertCallback;
        }

        @Override
        protected Long doInBackground(Void... params) {
            return mAction.insert();
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            if(id != -1){
                mWordDBObservable.notifyChanged();
                if(mInsertCallback != null){
                    mInsertCallback.insertSuccess(id);
                }

            }else{
                if(mInsertCallback != null){
                    mInsertCallback.insertFailed();
                }
            }
        }
    }


    public interface UpdateDeleteAction{
        Integer modify();
    }


    public interface UpdateDeleteCallback{

        void modificationSuccess(Integer count);

        void modificationFailed();
    }



    private class UpdateDeleteTask extends AsyncTask<Void,Void,Integer>{

        UpdateDeleteAction mAction;
        UpdateDeleteCallback  mUpdateDeleteCallback;

        public UpdateDeleteTask(UpdateDeleteAction action, UpdateDeleteCallback updateDeleteCallback) {
            mAction = action;
            mUpdateDeleteCallback = updateDeleteCallback;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return mAction.modify();
        }

        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            if(count > 0){
                mWordDBObservable.notifyChanged();
                if(mUpdateDeleteCallback != null){
                    mUpdateDeleteCallback.modificationSuccess(count);
                }

            }else{
                if(mUpdateDeleteCallback != null){
                    mUpdateDeleteCallback.modificationFailed();
                }
            }
        }
    }
}

package it.matteoavanzini.comelit.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by emme on 21/05/2018.
 */

public abstract class LoadDatabaseTask<T> extends AsyncTask<Void, Integer, List<T>> {

    private Context mContext;
    private ProgressDialog progressDialog;
    private final String TAG = LoadDatabaseTask.class.getName();

    public LoadDatabaseTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPreExecute() {
        progressDialog = getProgressDialog();
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(List<T> result) {
        progressDialog.dismiss();
        onTaskEnd(result);
    }

    protected abstract void onTaskEnd(List<T> result);

    private ProgressDialog getProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait....");
        progressDialog.setTitle("Loading posts");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progressDialog;
    }
}

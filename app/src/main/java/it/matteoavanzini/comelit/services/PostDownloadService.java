package it.matteoavanzini.comelit.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import it.matteoavanzini.comelit.PostListActivity;
import it.matteoavanzini.comelit.R;
import it.matteoavanzini.comelit.database.CommentDatabase;
import it.matteoavanzini.comelit.database.PostDatabase;
import it.matteoavanzini.comelit.model.Comment;
import it.matteoavanzini.comelit.model.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by emme on 21/05/2018.
 */

public class PostDownloadService extends Service {

    public final static String ACTION_POST_DOWNLOADED = "it.matteoavanzini.comelit.action.POST_DOWNLOADED";
    public final static String ACTION_DOWNLOAD_POST = "it.matteoavanzini.comelit.action.DOWNLOAD_POST";
    public final static String ACTION_DOWNLOAD_COMMENT = "it.matteoavanzini.comelit.action.DOWNLOAD_COMMENT";

    private static final String TAG = PostDownloadService.class.getName();

    private int NOTIFICATION = R.string.app_name;
    private Retrofit mRetrofit;
    private PostDatabase postDb;
    private NotificationManager mNotificator;

    @Override
    public void onCreate() {
        super.onCreate();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        postDb = Room.databaseBuilder(this,
                PostDatabase.class, "database-post").build();
        mNotificator = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        PostDownloadService getService() {
            return PostDownloadService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        String action = intent.getAction();

        if (action.equals(ACTION_DOWNLOAD_COMMENT)) {
            downloadComments();
        } else if (action.equals(ACTION_DOWNLOAD_POST)) {
            downloadPosts();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        // mNotificator.cancel(NOTIFICATION);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();


    private void downloadComments() {
        Log.d(TAG, "Start download comments");
        JsonPlaceHolderService service = mRetrofit.create(JsonPlaceHolderService.class);
        Call<List<Comment>> callComments = service.getAllComments();
        callComments.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                List<Comment> comments = response.body();

                Comment[] commentsArray = new Comment[comments.size()];
                for (int i =0; i<comments.size(); i++) {
                    commentsArray[i] = comments.get(i);
                }
                InsertCommentTask task = new InsertCommentTask();
                task.execute(commentsArray);

                Log.d(TAG, "End download comments");
                stopSelf();
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
                stopSelf();
            }
        });
    }

    private void downloadPosts() {
        Log.d(TAG, "Start download posts");
        JsonPlaceHolderService service = mRetrofit.create(JsonPlaceHolderService.class);
        Call<List<Post>> call = service.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postList = response.body();
                Post[] postArray = new Post[postList.size()];
                for (int i =0; i<postList.size(); i++) {
                    postArray[i] = postList.get(i);
                }

                InsertPostTask task = new InsertPostTask();
                task.execute(postArray);

                Log.d(TAG, "End download posts");
                stopSelf();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e(TAG, t.getLocalizedMessage());
                stopSelf();
            }
        });
    }

    private void sendPostBroadcast() {
        Intent intent = new Intent();
        intent.setAction(ACTION_POST_DOWNLOADED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void sendNotification() {

        Intent openApp = new Intent(this, PostListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openApp, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "DEFAULT-CHANNEL")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Ci sono nuovi post da leggere!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = mBuilder.build();
        mNotificator.notify(NOTIFICATION, notification);
    }

    private class InsertPostTask extends AsyncTask<Post, Void, Void> {

        @Override
        protected Void doInBackground(Post... params) {
            postDb.postDao().truncate();
            postDb.postDao().insertAll(params);

            if (isAppRunning(PostDownloadService.this, "it.matteoavanzini.comelit")) {
                sendPostBroadcast();
            } else {
                sendNotification();
            }

            return null;
        }
    }

    private class InsertCommentTask extends AsyncTask<Comment, Void, Void> {

        @Override
        protected Void doInBackground(Comment... comments) {

            CommentDatabase db = new CommentDatabase(PostDownloadService.this);
            db.truncate();
            db.insert(comments);
            db.close();
            return null;
        }
    }

    public boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


}

package fi.ptm.threadsexample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 * @author PTM
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*** Big wait -> ANR dialog will be shown ***/

    // method creates a long wait to demonstrate ANR (Application Not Responsive)
    // ->
    // ANR dialog will be shown
    public void longWaitClicked(View view) {
        try {
            Thread.sleep(1000*50);
        } catch (InterruptedException e)    {

        }
    }

    /*** Big wait -> ANR dialog will be shown ends ***/


    /*** Access UI Thread from another thread wrongly ***/


    // method demonstrate to create own thread and access UI from there wrongly
    // ->
    // android.view.ViewRootImpl$CalledFromWrongThreadException:
    // Only the original thread that created a view hierarchy can touch its views.
    public void UIWronglyClicked(View view) {
        DoSomethingWrongThread thread = new DoSomethingWrongThread();
        thread.start();
    }

    class DoSomethingWrongThread extends Thread {

        @Override
        public void run() {
            TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
            // you can't access UI elements from another thread directly!!
            infoTextView.setText("Info: Text from DoSomethingWrongThread");
        }

    }

    /*** Access UI Thread from another thread wrongly ends ***/


    /*** runOnUiThread part starts ***/

    public void runOnUiThreadClicked(View view) {
        runOnUiThread(runnable);
    }

    // create runnable object
    private Runnable runnable = new Runnable() {
        public void run() {
            TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
            // now you access UI elements
            infoTextView.setText("Info: Text from runOnUiThread");
        }
    };

    /*** runOnUiThread part ends ***/

    /*** View.post() part starts ***/

    public void viewPostClicked(View view) {
        final TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
        infoTextView.post(new Runnable() {
            @Override
            public void run() {
                // now you access UI elements
                infoTextView.setText("Info: Text from View.post()");
            }
        });

    }

    /*** View.post() part ends ***/

    /*** Handler message starts ***/

    public void handlerMessageClicked(View view) {
        // create handler object
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // now you access UI elements
                TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
                infoTextView.setText("Info: Text from Handler.");
            }
        });
    }

    /*** Handler message starts ***/

    /*** AsyncTask starts ***/

    private Button asyncTaskButton;
    private ProgressBar progressBar;

    public void asyncTaskClicked(View view) {
        // disable asynctaskbutton
        asyncTaskButton = (Button) findViewById(R.id.button6);
        asyncTaskButton.setEnabled(false);
        // find progressbar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // start asynctask
        new MyTask().execute();
    }

    // own class is extending AsyncTask
    private class MyTask extends AsyncTask<Void,Integer,Void> {

        // will be called first, can modify UI
        @Override
        protected void onPreExecute() {}

        // called 2nd, can't modify UI
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 1; i <= 100; i++) {
                // call to modify UI
                publishProgress(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // don,t use Toast (no UI modify)
                    Log.e("ERROR", "Thread Interrupted in AsyncTask");
                }
            };
            return null;
        }

        // called from doInBackground with publishProgress, can modify UI
        @Override
        protected void onProgressUpdate(Integer... parameters) {
            progressBar.setProgress(parameters[0]);
        }

        // called after doInBackground finished
        @Override
        protected void onPostExecute(Void parameters) {
            asyncTaskButton.setEnabled(true);
        }

        // called if asynctask is cancelled with cancel(true)
        @Override
        protected  void onCancelled() {

        }

    }
    
    /*** AsyncTask ends ***/

}

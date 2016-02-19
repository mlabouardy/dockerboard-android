package labouardy.com.dockerboard;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Internal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import labouardy.com.dockerboard.dialog.Dialog;
import labouardy.com.dockerboard.dialog.ErrorDialog;
import labouardy.com.dockerboard.dialog.SuccessDialog;
import labouardy.com.dockerboard.handler.APIHandler;
import labouardy.com.dockerboard.handler.InternalStorage;
import labouardy.com.dockerboard.model.Docker;
import labouardy.com.dockerboard.scanner.IntentIntegrator;
import labouardy.com.dockerboard.scanner.IntentResult;


public class Home extends ActionBarActivity {
    private TextView osTV, runningTV, stoppedTV, imagesTV;
    private RelativeLayout containersRL, imagesRL, dockerRL, emptyRL;
    private final String KEY="DOCKER_API";
    private InternalStorage storage=InternalStorage.getInstance();
    private boolean validAPI=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        changeColor();

        init();

        try {
            Docker o=(Docker)storage.readObject(this, KEY);
            if(o==null){
                emptyRL.setVisibility(View.VISIBLE);
            }else{
                emptyRL.setVisibility(View.GONE);
                Docker docker=Docker.getInstance();
                docker.signIn(o.getAPI(), o.getToken());
                getInfo();
            }
        } catch (IOException e) {
            emptyRL.setVisibility(View.VISIBLE);
        } catch (ClassNotFoundException e) {
            emptyRL.setVisibility(View.VISIBLE);
        }


    }

    public void init(){
        osTV=(TextView)findViewById(R.id.os);
        runningTV=(TextView)findViewById(R.id.running);
        stoppedTV=(TextView)findViewById(R.id.stopped);
        imagesTV=(TextView)findViewById(R.id.images);
        containersRL=(RelativeLayout)findViewById(R.id.runningHeader);
        imagesRL=(RelativeLayout)findViewById(R.id.imagesHeader);
        dockerRL=(RelativeLayout)findViewById(R.id.osHeader);
        emptyRL=(RelativeLayout)findViewById(R.id.emptyAPI);

        containersRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Containers.class);
                startActivity(i);
            }
        });

        imagesRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, Images.class);
                startActivity(i);
            }
        });

        dockerRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this, DockerDetail.class);
                startActivity(i);
            }
        });
    }

    public void scan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(Home.this);
        scanIntegrator.setMessage("Scan QRCode from Dockerboard app");
        scanIntegrator.initiateScan();
    }

    public void getInfo(){
        APIHandler apiHandler=APIHandler.getInstance();
        apiHandler.details(new Callback() {
            @Override
            public void onFailure(final Request request, IOException e) {
                validAPI=false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog=new ErrorDialog(Home.this);
                        dialog.show("Server is not responding");
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String data = response.body().string();
                validAPI=true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String os = "";
                        int running=0;
                        int stopped=0;
                        int images=0;
                        try {
                            JSONObject tmp=new JSONObject(data);
                            os=tmp.getString("OperatingSystem");
                            running=tmp.getInt("running");
                            stopped=tmp.getInt("Containers");
                            images=tmp.getInt("Images");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        emptyRL.setVisibility(View.GONE);

                        osTV.setText(os);
                        runningTV.setText(String.valueOf(running));
                        stoppedTV.setText(String.valueOf(stopped));
                        imagesTV.setText(String.valueOf(images));

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(scanningResult!=null){
            if(data!=null){
                String scanContent = scanningResult.getContents();

                try {
                    JSONObject dataAPI=new JSONObject(scanContent);
                    Docker docker= Docker.getInstance();
                    docker.signIn(dataAPI.getString("api"), dataAPI.getString("token"));
                    try {
                        storage.writeObject(this, KEY, docker);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    getInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void changeColor(){
        int color = getResources().getColor(R.color.dashboard);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.scan) {
            scan();
            return true;
        }

        if(id==R.id.refresh){
            if(validAPI){
                APIHandler apiHandler=APIHandler.getInstance();
                apiHandler.details(new Callback() {
                    @Override
                    public void onFailure(final Request request, IOException e) {
                        validAPI = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog dialog = new ErrorDialog(Home.this);
                                dialog.show("Server is not responding");
                            }
                        });
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        final String data = response.body().string();
                        validAPI = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String os = "";
                                int running = 0;
                                int stopped = 0;
                                int images = 0;
                                try {
                                    JSONObject tmp = new JSONObject(data);
                                    os = tmp.getString("OperatingSystem");
                                    running = tmp.getInt("running");
                                    stopped = tmp.getInt("Containers");
                                    images = tmp.getInt("Images");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                emptyRL.setVisibility(View.GONE);

                                osTV.setText(os);
                                runningTV.setText(String.valueOf(running));
                                stoppedTV.setText(String.valueOf(stopped));
                                imagesTV.setText(String.valueOf(images));

                                Dialog dialog=new SuccessDialog(Home.this);
                                dialog.show("Information has been updated");

                            }
                        });
                    }
                });
            }else{
                Dialog dialog=new ErrorDialog(Home.this);
                dialog.show("Server is not responding");
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

package labouardy.com.dockerboard;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import labouardy.com.dockerboard.dialog.Dialog;
import labouardy.com.dockerboard.dialog.ErrorDialog;
import labouardy.com.dockerboard.handler.APIHandler;


public class DockerDetail extends ActionBarActivity {
    private TextView dockerTV, apiTV, osTV, archTV, kernelTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docker_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeColor();

        init();
    }

    public void init(){
        dockerTV=(TextView)findViewById(R.id.docker);
        apiTV=(TextView)findViewById(R.id.api);
        osTV=(TextView)findViewById(R.id.os);
        archTV=(TextView)findViewById(R.id.arch);
        kernelTV=(TextView)findViewById(R.id.kernel);

        APIHandler apiHandler=APIHandler.getInstance();
        apiHandler.version(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog=new ErrorDialog(DockerDetail.this);
                        dialog.show("Server is not responding");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String data=response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject tmp=new JSONObject(data);
                            String docker=tmp.getString("Version");
                            String api=tmp.getString("ApiVersion");
                            String os=tmp.getString("Os");
                            String arch=tmp.getString("Arch");
                            String kernel=tmp.getString("KernelVersion");

                            dockerTV.setText(docker);
                            apiTV.setText(api);
                            osTV.setText(os);
                            archTV.setText(arch);
                            kernelTV.setText(kernel);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_docker_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

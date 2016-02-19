package labouardy.com.dockerboard;

import android.content.Intent;
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
import java.text.DecimalFormat;

import labouardy.com.dockerboard.dialog.Dialog;
import labouardy.com.dockerboard.dialog.ErrorDialog;
import labouardy.com.dockerboard.handler.APIHandler;


public class ContainerDetail extends ActionBarActivity {
    private String id, image;
    private TextView imageTV, memoryTV, cpuTV, networkinTV, networkoutTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeColor();

        Intent intent=getIntent();
        image=intent.getStringExtra("image");
        id=intent.getStringExtra("id");

        init();


    }

    public void init(){
        imageTV=(TextView)findViewById(R.id.image);
        memoryTV=(TextView)findViewById(R.id.memory);
        cpuTV=(TextView)findViewById(R.id.cpu);
        networkinTV=(TextView)findViewById(R.id.networkIn);
        networkoutTV=(TextView)findViewById(R.id.networkOut);

        imageTV.setText(image);

        APIHandler apiHandler=APIHandler.getInstance();
        apiHandler.container(id, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog=new ErrorDialog(ContainerDetail.this);
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
                            Double cpu=Double.valueOf(tmp.getJSONObject("cpu_stats").getJSONObject("cpu_usage").getLong("total_usage") / 1000000);
                            Double cpu_system=Double.valueOf(tmp.getJSONObject("cpu_stats").getLong("system_cpu_usage") / 1000000);
                            Double cpu_usage=(cpu/cpu_system)*100;
                            String memory=(tmp.getJSONObject("memory_stats").getLong("usage")/1000000)+" Mb";
                            String cpu_container=(new DecimalFormat("#.##").format(cpu_usage))+" %";
                            String networkin=(tmp.getJSONObject("network").getLong("rx_bytes")/1000)+" Kb";;
                            String networkout=(tmp.getJSONObject("network").getLong("tx_bytes")/1000)+" Kb";;

                            memoryTV.setText(memory);
                            cpuTV.setText(cpu_container);
                            networkinTV.setText(networkin);
                            networkoutTV.setText(networkout);
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
        getMenuInflater().inflate(R.menu.menu_container_detail, menu);
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

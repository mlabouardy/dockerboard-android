package labouardy.com.dockerboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import labouardy.com.dockerboard.adapter.ContainerAdapter;
import labouardy.com.dockerboard.dialog.Dialog;
import labouardy.com.dockerboard.dialog.ErrorDialog;
import labouardy.com.dockerboard.handler.APIHandler;
import labouardy.com.dockerboard.model.Container;


public class Containers extends ActionBarActivity {
    private ListView lv;
    private ContainerAdapter containerAdapter;
    private List<Container> containers;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_containers);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        changeColor();

        init();
    }

    public void init(){
        containers=new ArrayList<>();
        lv=(ListView)findViewById(R.id.listOfContainers);
        containerAdapter=new ContainerAdapter(this, containers);
        lv.setAdapter(containerAdapter);
        progressDialog=new ProgressDialog(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(Containers.this, ContainerDetail.class);
                intent.putExtra("id", containers.get(i).getId());
                intent.putExtra("image", containers.get(i).getImage());
                startActivity(intent);
            }
        });

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        APIHandler apiHandler=APIHandler.getInstance();
        apiHandler.containers(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog=new ErrorDialog(Containers.this);
                        dialog.show("Server is not responding");
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String data=response.body().string();
                try {
                    JSONArray tmp=new JSONArray(data);
                    for(int i=0;i<tmp.length();i++){
                        JSONObject o=tmp.getJSONObject(i);
                        String Id=o.getString("Id");
                        String image=o.getString("Image");
                        String time=o.getString("Status");
                        String name=o.getJSONArray("Names").get(0).toString();
                        name=name.substring(1);

                        Container container=new Container();
                        container.setId(Id);
                        container.setImage(image);
                        container.setTime(time);
                        container.setContainer(name);

                        containers.add(container);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        containerAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
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
        getMenuInflater().inflate(R.menu.menu_containers, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}

package labouardy.com.dockerboard;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import labouardy.com.dockerboard.adapter.ContainerAdapter;
import labouardy.com.dockerboard.adapter.ImageAdapter;
import labouardy.com.dockerboard.dialog.Dialog;
import labouardy.com.dockerboard.dialog.ErrorDialog;
import labouardy.com.dockerboard.handler.APIHandler;
import labouardy.com.dockerboard.model.Container;
import labouardy.com.dockerboard.model.Image;


public class Images extends ActionBarActivity {
    private ListView lv;
    private ImageAdapter imageAdapter;
    private List<Image> images;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeColor();

        init();
    }

    public void init(){
        images=new ArrayList<>();
        lv=(ListView)findViewById(R.id.listOfImages);
        imageAdapter=new ImageAdapter(this, images);
        lv.setAdapter(imageAdapter);
        progressDialog=new ProgressDialog(this);

        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        APIHandler apiHandler=APIHandler.getInstance();
        apiHandler.images(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Dialog dialog=new ErrorDialog(Images.this);
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

                        String repo=o.getJSONArray("RepoTags").get(0).toString();

                        String id=o.getString("Id");
                        String name=repo.substring(0, repo.indexOf(":"));
                        String tag=repo.substring(repo.indexOf(":")+1);
                        DecimalFormat d=new DecimalFormat("#.##");
                        Double size=Double.valueOf(d.format(o.getDouble("VirtualSize")/1000000));

                        Image image=new Image();
                        image.setId(id);
                        image.setName(name);
                        image.setTag(tag);
                        image.setSize(size);

                        images.add(image);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.menu_images, menu);
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

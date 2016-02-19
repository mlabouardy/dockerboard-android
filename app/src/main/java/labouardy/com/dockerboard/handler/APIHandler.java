package labouardy.com.dockerboard.handler;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import labouardy.com.dockerboard.model.Docker;

/**
 * Created by mlabouardy on 17/02/16.
 */
public class APIHandler {
    private OkHttpClient client;
    private static APIHandler instance;
    private Docker docker= Docker.getInstance();

    private APIHandler(){
        client=new OkHttpClient();
    }

    public static APIHandler getInstance(){
        if(instance==null)
            instance=new APIHandler();
        return instance;
    }

    public Call containers(Callback callback){
        Request request=new Request.Builder().url(docker.getAPI()+"/containers").build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call images(Callback callback){
        Request request=new Request.Builder().url(docker.getAPI()+"/images").build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call details(Callback callback){
        Request request=new Request.Builder().url(docker.getAPI()+"/info").build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call version(Callback callback){
        Request request=new Request.Builder().url(docker.getAPI()+"/version").build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Call container(String id, Callback callback){
        Request request=new Request.Builder().url(docker.getAPI()+"/usage/"+id).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

}

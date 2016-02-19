package labouardy.com.dockerboard.model;

import java.io.Serializable;

/**
 * Created by mlabouardy on 17/02/16.
 */
public class Docker implements Serializable{
    private static Docker instance;
    private String api;
    private String token;

    private Docker(){}

    public static Docker getInstance(){
        if(instance==null)
            instance=new Docker();
        return instance;
    }

    public void signIn(String api, String token){
        this.api=api;
        this.token=token;
    }

    public String getAPI(){
        return api;
    }

    public String getToken(){
        return token;
    }

    @Override
    public String toString() {
        return api+" "+token;
    }


}

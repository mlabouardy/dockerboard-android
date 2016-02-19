package labouardy.com.dockerboard.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import labouardy.com.dockerboard.model.Base;


/**
 * Created by mlabouardy on 18/02/16.
 */
public abstract class AbstractAdapter extends BaseAdapter {
    protected List<? extends Base> entities;
    protected Activity activity;

    public AbstractAdapter(Activity activity, List<? extends Base> entities){
        this.activity=activity;
        this.entities=entities;
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Object getItem(int i) {
        return entities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}

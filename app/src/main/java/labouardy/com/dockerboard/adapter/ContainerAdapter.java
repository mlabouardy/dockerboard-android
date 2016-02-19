package labouardy.com.dockerboard.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import labouardy.com.dockerboard.R;
import labouardy.com.dockerboard.model.Container;

/**
 * Created by mlabouardy on 18/02/16.
 */
public class ContainerAdapter extends AbstractAdapter {

    public ContainerAdapter(Activity activity, List<Container> containers) {
        super(activity, containers);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Container container=(Container)entities.get(i);

        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.container, null);

        TextView containerTV=(TextView)view.findViewById(R.id.containerTV);
        TextView imageTV=(TextView)view.findViewById(R.id.imageTV);
        TextView timeTV=(TextView)view.findViewById(R.id.timeTV);

        containerTV.setText(container.getContainer());
        imageTV.setText(container.getImage());
        timeTV.setText(container.getTime());

        return view;
    }
}

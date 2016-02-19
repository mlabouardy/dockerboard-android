package labouardy.com.dockerboard.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import labouardy.com.dockerboard.R;
import labouardy.com.dockerboard.model.Container;
import labouardy.com.dockerboard.model.Image;

/**
 * Created by mlabouardy on 18/02/16.
 */
public class ImageAdapter extends AbstractAdapter {

    public ImageAdapter(Activity activity, List<Image> images) {
        super(activity, images);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Image image=(Image)entities.get(i);

        LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.image, null);

        TextView imageTV=(TextView)view.findViewById(R.id.imageTV);
        TextView tagTV=(TextView)view.findViewById(R.id.tagTV);
        TextView sizeTV=(TextView)view.findViewById(R.id.sizeTV);

        imageTV.setText(image.getName());
        tagTV.setText(image.getTag());
        sizeTV.setText(String.valueOf(image.getSize()) + " Mb");

        return view;
    }
}

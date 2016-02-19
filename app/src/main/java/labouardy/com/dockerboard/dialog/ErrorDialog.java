package labouardy.com.dockerboard.dialog;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mlabouardy on 25/12/15.
 */
public class ErrorDialog extends Dialog {

    public ErrorDialog(Context context) {
        super(new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE), "Dockerboard");
    }
}
package labouardy.com.dockerboard.dialog;

import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mlabouardy on 25/12/15.
 */
public class SuccessDialog extends Dialog {

    public SuccessDialog(Context context) {
        super(new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE), "Dockerboard");
    }
}

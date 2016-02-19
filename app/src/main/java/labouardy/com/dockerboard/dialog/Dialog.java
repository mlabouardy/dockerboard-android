package labouardy.com.dockerboard.dialog;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mlabouardy on 25/12/15.
 */
public class Dialog {
    private SweetAlertDialog dialog;
    private String title;
    public Dialog(SweetAlertDialog dialog, String title){
        this.dialog=dialog;
        this.title=title;
    }

    public void show(String msg){
        dialog.setTitleText(title)
              .setContentText(msg)
              .show();
    }
}

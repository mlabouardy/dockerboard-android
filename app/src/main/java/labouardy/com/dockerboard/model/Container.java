package labouardy.com.dockerboard.model;

/**
 * Created by mlabouardy on 18/02/16.
 */
public class Container extends Base{
    private String container;
    private String image;
    private String time;

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

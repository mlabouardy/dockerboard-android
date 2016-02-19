package labouardy.com.dockerboard.model;

/**
 * Created by mlabouardy on 18/02/16.
 */
public class Image extends Base {
    private String name;
    private String tag;
    private Double size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }
}

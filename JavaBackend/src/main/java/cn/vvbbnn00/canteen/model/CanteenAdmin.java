package cn.vvbbnn00.canteen.model;

import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;

@Data
@JavaBean
public class CanteenAdmin implements Serializable {
    private Integer canteenId;
    private Integer userId;
}

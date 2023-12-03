package cn.vvbbnn00.canteen.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanteenAdmin implements Serializable {
    private Integer canteenId;
    private Integer userId;
}

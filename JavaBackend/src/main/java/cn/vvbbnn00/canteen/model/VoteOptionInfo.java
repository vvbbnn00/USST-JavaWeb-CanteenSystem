package cn.vvbbnn00.canteen.model;

import lombok.Data;

import java.beans.JavaBean;

@Data
@JavaBean
public class VoteOptionInfo {
    private Integer voteOptionId;
    private String name;
    private Integer count;

}

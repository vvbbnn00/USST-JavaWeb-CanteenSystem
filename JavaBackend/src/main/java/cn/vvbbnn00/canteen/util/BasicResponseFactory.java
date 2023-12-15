package cn.vvbbnn00.canteen.util;

import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.dto.response.BasicResponse;

import java.util.List;

public class BasicResponseFactory {
    public static BasicResponse createBasicResponse(int code, String message) {
        BasicResponse basicResponse = new BasicResponse();
        basicResponse.setCode(code);
        basicResponse.setMessage(message);
        return basicResponse;
    }

    public static BasicDataResponse createBasicDataResponse(int code, String message, Object data) {
        BasicDataResponse basicDataResponse = new BasicDataResponse();
        basicDataResponse.setCode(code);
        basicDataResponse.setMessage(message);
        basicDataResponse.setData(data);
        return basicDataResponse;
    }

    public static BasicDataResponse createBasicDataResponse(int code, String message) {
        BasicDataResponse basicDataResponse = new BasicDataResponse();
        basicDataResponse.setCode(code);
        basicDataResponse.setMessage(message);
        return basicDataResponse;
    }

    public static BasicListResponse createBasicListResponse(int code, String message, List<?> list, int total) {
        BasicListResponse basicListResponse = new BasicListResponse();
        basicListResponse.setCode(code);
        basicListResponse.setMessage(message);
        basicListResponse.setList(list);
        basicListResponse.setTotal(total);
        return basicListResponse;
    }
}
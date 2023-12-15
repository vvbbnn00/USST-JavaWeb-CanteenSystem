package cn.vvbbnn00.canteen.filter;

import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        // 构建自定义响应
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new BasicResponse(400, exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

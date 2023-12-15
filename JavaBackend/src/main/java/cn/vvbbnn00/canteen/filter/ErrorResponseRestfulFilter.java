package cn.vvbbnn00.canteen.filter;

import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.util.GsonFactory;
import com.google.gson.Gson;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.container.ContainerResponseContext;

import java.io.IOException;

@Provider
public class ErrorResponseRestfulFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getStatus() >= 400) { // 检查是否为错误响应
//            responseContext.setEntity(GsonFactory.generateErrorResponse(
//                            responseContext.getStatus(),
//                            responseContext.getStatusInfo().getReasonPhrase()
//                    ),
//                    responseContext.getEntityAnnotations(),
//                    MediaType.APPLICATION_JSON_TYPE);
        }
    }
}
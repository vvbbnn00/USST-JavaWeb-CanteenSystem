package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.LogUtils;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/hello")
public class HelloResource {
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/{id}")
    @Produces("application/json")
    @CheckRole("admin")
    public String hello(@PathParam("id") String id) {
        String username = securityContext.getUserPrincipal().getName();
        BasicDataResponse response = new BasicDataResponse();
        response.setMessage("Hello, " + username + "!");
        response.setData(id);
        return GsonFactory.getGson().toJson(response);
    }
}
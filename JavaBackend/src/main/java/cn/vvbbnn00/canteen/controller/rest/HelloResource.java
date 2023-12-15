package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.BasicResponseFactory;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/hello")
@RequestScoped
public class HelloResource {
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse hello(
            @NotNull @Min(value = 5, message = "Invalid Id") @PathParam("id") Integer id) {
        String username = securityContext.getUserPrincipal().getName();
        BasicDataResponse response = new BasicDataResponse();
        response.setMessage("Hello, " + username + "!");
        response.setData(id);
        return response;
    }

    @POST
    @Path("/post")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse helloPost(User user) {
        try {
            RequestValidatorUtils.doHibernateValidate(user);
        } catch (Exception e) {
            return BasicResponseFactory.createBasicDataResponse(400, e.getMessage());
        }
        String username = securityContext.getUserPrincipal().getName();
        BasicDataResponse response = new BasicDataResponse();
        response.setMessage("Hello, " + username + "!");
        response.setData(user.getUserId());
        return response;
    }
}
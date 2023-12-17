package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.ImageGenerationResponse;
import cn.vvbbnn00.canteen.util.MinioUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/image")
@RequestScoped
public class ImageResource {
    @POST
    @Path("/upload")
    @Produces("application/json")
    @CheckRole("user")
    public ImageGenerationResponse uploadImage() {
        String fileKey = MinioUtils.generateFileKey();
        ImageGenerationResponse imageGenerationResponse = new ImageGenerationResponse();
        imageGenerationResponse.setFileKey(fileKey);
        String uploadUrl = MinioUtils.generateUploadUrl(fileKey);
        imageGenerationResponse.setUrl(uploadUrl);
        return imageGenerationResponse;
    }
}

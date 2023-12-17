package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.ImageDao;
import cn.vvbbnn00.canteen.dao.impl.ImageDaoImpl;
import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import cn.vvbbnn00.canteen.model.Image;
import cn.vvbbnn00.canteen.util.MinioUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ImageService {
    private final ImageDao imageDao = new ImageDaoImpl();

    /**
     * 获取图片信息的函数
     *
     * @param fileKey 文件名
     * @param doCheck 是否检查文件是否存在
     * @return 如果文件存在，返回ImageInfoResponse对象，包含图片的信息。如果文件不存在，返回null
     */
    public ImageInfoResponse getImageInfo(String fileKey, boolean doCheck) {
        if (doCheck && !ifImageExist(fileKey)) {
            return null;
        }
        ImageInfoResponse imageInfoResponse = new ImageInfoResponse();
        imageInfoResponse.setFileKey(fileKey);
        imageInfoResponse.setOriginalUrl(MinioUtils.generateDownloadUrl(fileKey + ".webp"));
        imageInfoResponse.setX128Url(MinioUtils.generateDownloadUrl(fileKey + "_128.webp"));
        imageInfoResponse.setX256Url(MinioUtils.generateDownloadUrl(fileKey + "_256.webp"));
        imageInfoResponse.setX384Url(MinioUtils.generateDownloadUrl(fileKey + "_384.webp"));
        return imageInfoResponse;
    }

    /**
     * 判断图片是否存在
     *
     * @param fileKey 文件名
     * @return 如果文件存在，返回true。如果文件不存在，返回false
     */
    public boolean ifImageExist(String fileKey) {
        return MinioUtils.isFileExist(fileKey + ".webp");
    }

    public Image addImage(Image image) {
        return imageDao.insert(image);
    }

    /**
     * 获取图片信息列表
     *
     * @param type        图片类型
     * @param referenceId 引用ID
     * @param doCheck     是否检查文件是否存在
     * @return 返回图片信息列表
     */
    public List<ImageInfoResponse> getImageInfoList(Image.ImageType type, Integer referenceId, boolean doCheck) {
        List<Image> images = imageDao.queryByReference(type.toString(), referenceId);
        return images
                .stream()
                .map(image -> getImageInfo(image.getFileId(), doCheck))
                .collect(Collectors.toList());
    }


    /**
     * 删除图片
     *
     * @param type        图片类型
     * @param referenceId 引用ID
     * @return 返回删除是否成功
     */
    public boolean deleteReferenceImage(Image.ImageType type, Integer referenceId) {
        return imageDao.deleteByReference(type.toString(), referenceId);
    }

}
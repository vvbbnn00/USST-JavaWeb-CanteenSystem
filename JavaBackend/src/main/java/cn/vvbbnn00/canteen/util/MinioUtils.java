package cn.vvbbnn00.canteen.util;

import io.minio.GetObjectTagsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.http.Method;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

// MinioUtils类提供使用MinIO的工具
public class MinioUtils {
    // Bucket的名称从配置中获取
    private static final String BUCKET_NAME = ConfigUtils.getEnv("MINIO_BUCKET_NAME", "javaweb-canteen-system");
    private static final String MINIO_ENDPOINT = ConfigUtils.getEnv("MINIO_ENDPOINT", "http://localhost:9000");
    private static final String MINIO_ACCESS_KEY = ConfigUtils.getEnv("MINIO_ACCESS_KEY", "minioadmin");
    private static final String MINIO_SECRET_KEY = ConfigUtils.getEnv("MINIO_SECRET_KEY", "minioadmin");

    // 私有构造器防止实例化这个工具类
    private MinioUtils() {
    }

    // MinioClientHolder类用于存储MinioClient的实例
    private static final class MinioClientHolder {
        // MinioClient的实例只在类第一次加载的时候被创建
        private static final MinioClient minioClient = MinioClient.builder()
                // MinIO服务的endpoint，从配置中获取
                .endpoint(MINIO_ENDPOINT)
                // MinIO服务的access_key和secret_key，从配置中获取
                .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                .build();
    }

    /**
     * 获取MinioClient的实例
     *
     * @return MinioClient的实例
     */
    public static MinioClient getInstance() {
        return MinioClientHolder.minioClient;
    }

    /**
     * 创建一个新的文件键，这个文件键是随机生成的UUID，并加上一个"canteen_system_"的前缀。
     *
     * @return 新的文件键
     */
    public static String generateFileKey() {
        UUID uuid = UUID.randomUUID();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "canteen_system_" + timestamp + "_" + uuid;
    }

    /**
     * 生成一个MiniO的预签名URL，这个URL可以用于上传文件。
     * 这个上传的文件将会被一个自动脚本处理后删除。
     *
     * @param fileKey 文件的key
     * @return 上传文件的URL
     */
    public static String generateUploadUrl(String fileKey) {
        MinioClient minioClient = getInstance();
        fileKey += ".op"; // .op 用于标识这个文件是上传的，将会被自动脚本处理后删除
        try {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(BUCKET_NAME)
                    .object(fileKey)
                    .expiry(60 * 60) // 1 hour
                    .build();
            return minioClient.getPresignedObjectUrl(args);
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据文件的key，生成一个用于下载该文件的URL。
     * 这个URL不需要accessKey和secretKey，因此可以实现匿名下载。
     *
     * @param fileKey 文件的key
     * @return 下载文件的URL
     */
    public static String generateDownloadUrl(String fileKey) {
        // 目前支持匿名下载，所以不需要accessKey和secretKey
        return MINIO_ENDPOINT + "/" + BUCKET_NAME + "/" + fileKey;
    }

    /**
     * 判断文件是否存在，如果文件存在则返回true，否则返回false。
     *
     * @param fileKey 要检查的文件的key
     * @return 如果文件存在则返回true，否则返回false
     */
    public static Boolean isFileExist(String fileKey) {
        MinioClient minioClient = getInstance();
        try {
            minioClient.getObjectTags(GetObjectTagsArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fileKey)
                    .build()
            );
            return true;
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            return false;
        }
    }

}
package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.ItemDao;
import cn.vvbbnn00.canteen.dao.impl.ItemDaoImpl;
import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import cn.vvbbnn00.canteen.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ItemService {
    private static final CuisineService cuisineService = new CuisineService();
    private static final CanteenService canteenService = new CanteenService();
    private static final ImageService imageService = new ImageService();
    private static final CanteenAdminService canteenAdminService = new CanteenAdminService();
    private static final ItemDao itemDao = new ItemDaoImpl();
    private static final CommentService commentService = new CommentService();


    /**
     * 该函数用于添加菜品。首先，它会验证当前用户是否为餐厅管理员。
     * 然后，它会验证输入的菜品名称和图片是否为空，并且检查选择的菜系是否存在且属于该餐厅。
     * 最后，菜品被加入到数据库中，并且相应的图片也被存储。
     *
     * @param item      要添加的菜品项
     * @param userId    用户的ID
     * @param canteenId 餐厅的ID
     * @return 返回新加入的菜品项
     * @throws RuntimeException 当用户不是餐厅管理员、菜品名称为空、选择的菜系不存在或者不属于该餐厅、图片为空或者上传失败、或者创建菜品失败时，会抛出RuntimeException。
     */
    public Item addItem(Item item, Integer userId, Integer canteenId) {
        // 验证当前用户是否为餐厅管理员，如果不是则抛出异常
        if (!canteenAdminService.checkHasCanteenAdmin(canteenId, userId)) {
            throw new RuntimeException("您不是该餐厅的管理员");
        }

        // 检查菜品名称是否为空，如果是则抛出异常
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new RuntimeException("菜品名称不能为空");
        }

        // 根据id获取菜系，若为空则抛出异常
        Cuisine cuisine = cuisineService.getCuisineById(item.getCuisineId());
        if (cuisine == null) {
            throw new RuntimeException("菜系不存在");
        }

        // 检查所选菜系是否属于该餐厅，如果不是则抛出异常
        if (!Objects.equals(cuisine.getCanteenId(), canteenId)) {
            throw new RuntimeException("菜系不属于该餐厅");
        }

        String fileKey = item.getFileKey();

        // 检查图片是否为空，若是则抛出异常
        if (fileKey == null || fileKey.isEmpty()) {
            throw new RuntimeException("图片不能为空");
        }

        // 检查图片是否上传成功，若没有则抛出异常
        if (!imageService.ifImageExist(fileKey)) {
            throw new RuntimeException("图片上传失败，可能是图片格式问题导致转码失败");
        }

        // 往数据库中插入新的菜品
        Item result = itemDao.insert(item);
        if (result == null) {
            throw new RuntimeException("创建菜品失败");
        }

        try {
            Image image = new Image();
            image.setType(Image.ImageType.item);
            image.setFileId(fileKey);
            image.setReferenceId(result.getItemId());

            // 存储图片信息
            Image imageResult = imageService.addImage(image);
            if (imageResult == null) {
                throw new RuntimeException("创建菜品失败，图片上传失败");
            }
        } catch (Exception e) {
            // 如果图片存储失败，删除新加入的菜品
            itemDao.delete(result.getItemId());
            throw new RuntimeException(e.getMessage());
        }

        // 获取图片信息，并设置到返回的菜品项中
        ImageInfoResponse ifr = imageService.getImageInfo(fileKey, true);
        result.setImage(ifr);

        return result;
    }

    /**
     * 该函数用于更新菜品信息。首先，它会根据菜品的ID查询出旧的菜品信息。
     * 然后，验证当前用户是否为餐厅管理员，如果不是则抛出异常。
     * 接着，它对传入的菜品对象中的各个属性进行检查，如果某个属性值不为空，则将这个属性值更新到旧的菜品信息中。
     * 最后，它会根据新的菜品信息，更新数据库中的记录。
     *
     * @param item   要更新的菜品对象，其中包含了新的菜品信息。
     * @param userId 用户的ID
     * @return 如果更新成功则返回true，否则返回false。
     * @throws RuntimeException 当用户不是餐厅管理员、菜系不存在或者不属于该餐厅、图片为空或者上传失败、或者更新菜品失败时，会抛出RuntimeException。
     */
    public boolean updateItem(Item item, Integer userId) {
        // 根据菜品ID查询出旧的菜品信息
        Item itemExist = itemDao.queryById(item.getItemId());
        if (itemExist == null) {
            throw new RuntimeException("菜品不存在");
        }

        int canteenId = cuisineService.getCuisineById(itemExist.getCuisineId()).getCanteenId();

        // 验证当前用户是否为餐厅管理员，如果不是则抛出异常
        if (!canteenAdminService.checkHasCanteenAdmin(canteenId, userId)) {
            throw new RuntimeException("您不是该餐厅的管理员");
        }

        // 对传入的菜品对象中的各个属性进行检查，如果某个属性值不为空，则将这个属性值更新到旧的菜品信息中
        if (item.getName() != null && !item.getName().isEmpty()) {
            itemExist.setName(item.getName());
        }

        // 检查更新菜系ID，需要进行菜系的检查
        if (item.getCuisineId() != null) {
            Cuisine cuisine = cuisineService.getCuisineById(item.getCuisineId());
            if (cuisine == null) {
                throw new RuntimeException("菜系不存在");
            }
            if (!Objects.equals(cuisine.getCanteenId(), canteenId)) {
                throw new RuntimeException("菜系不属于该餐厅");
            }
            itemExist.setCuisineId(item.getCuisineId());
        }

        if (item.getPrice() != null) {
            itemExist.setPrice(item.getPrice());
        }

        if (item.getPromotionPrice() != null) {
            itemExist.setPromotionPrice(item.getPromotionPrice());
        }

        if (item.getIntroduction() != null) {
            itemExist.setIntroduction(item.getIntroduction());
        }

        if (item.getRecommended() != null) {
            itemExist.setRecommended(item.getRecommended());
        }

        String fileKey = item.getFileKey();
        String existFileKey = itemExist.getFileKey();
        if (fileKey != null && !fileKey.equals(existFileKey)) {
            // 检查图片是否为空，若是则抛出异常
            if (fileKey.isEmpty()) {
                throw new RuntimeException("图片不能为空");
            }

            // 检查图片是否上传成功，若没有则抛出异常
            if (!imageService.ifImageExist(fileKey)) {
                throw new RuntimeException("图片上传失败，可能是图片格式问题导致转码失败");
            }

            Image newImage = new Image();
            newImage.setReferenceId(item.getItemId());
            newImage.setType(Image.ImageType.item);
            newImage.setFileId(fileKey);

            // 删除旧的图片
            imageService.deleteReferenceImage(Image.ImageType.item, item.getItemId());

            // 存储新的图片
            Image imageResult = imageService.addImage(newImage);

            if (imageResult == null) {
                throw new RuntimeException("更新菜品失败，图片上传失败");
            }

        }

        // 往数据库中插入新的菜品
        boolean result = itemDao.update(itemExist);
        if (!result) {
            throw new RuntimeException("更新菜品失败");
        }

        return true;
    }

    /**
     * 该函数用于根据菜品ID获取菜品信息。首先，它会调用itemDao的queryById函数，根据菜品ID查询菜品信息。<br>
     * 如果查询结果为null，函数直接返回null。然后，它会调用imageService的getImageInfo函数，根据菜品的fileKey获取菜品图片的信息，并将图片信息设置到菜品对象中。<br>
     * 接着，它会调用cuisineService的getCuisineById函数，根据菜品的cuisineId获取菜系信息，并将菜系信息设置到菜品对象中。<br>
     * 最后，它会调用canteenService的getCanteenById函数，根据菜系的canteenId获取餐厅信息，并将餐厅信息设置到菜品对象中。<br>
     * 函数返回完全填充了信息的菜品对象。<br>
     *
     * @param itemId 要获取的菜品的ID
     * @return 返回的菜品对象，如果不存在，则返回null
     */
    public Item getItemById(Integer itemId) {
        Item item = itemDao.queryById(itemId);
        if (item == null) {
            return null;
        }
        List<ImageInfoResponse> imageInfoResponses = imageService.getImageInfoList(Image.ImageType.item, itemId, false);
        if (!imageInfoResponses.isEmpty()) {
            item.setImage(imageInfoResponses.get(0));
        }
        Cuisine cuisine = cuisineService.getCuisineById(item.getCuisineId());
        item.setCuisine(cuisine);
        Canteen canteen = canteenService.getCanteenById(cuisine.getCanteenId());
        item.setCanteen(canteen);
        return item;
    }

    /**
     * 获取菜品列表
     *
     * @param kw            关键词
     * @param cuisineId     菜系Id
     * @param canteenId     食堂Id
     * @param isRecommended 是否推荐
     * @param page          页码
     * @param pageSize      每页大小
     * @param orderBy       排序依据
     * @param asc           是否升序
     * @return 返回查询到的菜品列表
     */
    public List<Item> getItemList(String kw, Integer cuisineId, Integer canteenId, Boolean isRecommended,
                                  Integer page, Integer pageSize, String orderBy, Boolean asc) {
        List<Item> itemList = itemDao.query(kw, cuisineId, canteenId, isRecommended, page, pageSize, orderBy, asc);
        for (Item item : itemList) {
            item.setImage(imageService.getImageInfo(item.getFileKey(), false));
        }

        return itemList;
    }

    /**
     * 获取菜品数量
     *
     * @param kw            关键词
     * @param cuisineId     菜系Id
     * @param canteenId     食堂Id
     * @param isRecommended 是否推荐
     * @return 返回查询到的菜品数量
     */
    public Integer getItemListCount(String kw, Integer cuisineId, Integer canteenId, Boolean isRecommended) {
        return itemDao.count(kw, cuisineId, canteenId, isRecommended);
    }

    /**
     * 删除菜品
     *
     * @param itemId 要删除的菜品的ID
     * @param userId 用户的ID
     * @throws RuntimeException 当用户不是餐厅管理员、菜品不存在或者删除菜品失败时，会抛出RuntimeException。
     */
    public void deleteItem(Integer itemId, Integer userId) {
        Item item = itemDao.queryById(itemId);
        if (item == null) {
            throw new RuntimeException("菜品不存在");
        }
        int canteenId = cuisineService.getCuisineById(item.getCuisineId()).getCanteenId();
        if (!canteenAdminService.checkHasCanteenAdmin(canteenId, userId)) {
            throw new RuntimeException("您不是该餐厅的管理员");
        }
        boolean result = itemDao.delete(itemId);
        if (!result) {
            throw new RuntimeException("删除菜品失败");
        }
    }


    public void commentItem(String content, Integer itemId, Integer userId, Integer parentId, BigDecimal score) {
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }
        if (itemId == null) {
            throw new RuntimeException("评论的菜品不能为空");
        }
        if (userId == null) {
            throw new RuntimeException("用户不能为空");
        }
        if (score == null) {
            throw new RuntimeException("评分不能为空");
        }
        Comment commentToInsert = new Comment();
        commentToInsert.setContent(content);
        commentToInsert.setReferenceId(itemId);
        commentToInsert.setParentId(parentId);
        commentToInsert.setType(Comment.CommentType.item);
        commentToInsert.setScore(score);
        commentToInsert.setCreatedBy(userId);
        try {
            commentService.createComment(commentToInsert, userId);
        } catch (Exception e) {
            throw new RuntimeException("评论失败");
        }
    }
}
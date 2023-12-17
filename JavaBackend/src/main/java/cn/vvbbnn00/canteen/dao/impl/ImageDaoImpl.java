package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.ImageDao;
import cn.vvbbnn00.canteen.model.Image;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ImageDaoImpl implements ImageDao {
    @Override
    public Image insert(Image image) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, image, new String[]{
                    "fileId", "type", "referenceId"
            });
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return queryById(rs.getInt(1));
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Image> queryByReference(String type, Integer referenceId) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Image.class, new String[]{
                "imageId", "fileId", "type", "referenceId", "createdAt", "updatedAt"
        });
        sql += " WHERE type = ? AND reference_id = ?";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, type);
            ps.setInt(2, referenceId);
            ResultSet rs = ps.executeQuery();
            List<Image> images = new ArrayList<>();
            while (rs.next()) {
                images.add((Image) SqlStatementUtils.makeEntityFromResult(rs, Image.class));
            }
            return images;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Image queryById(Integer imageId) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Image.class, new String[]{
                "imageId", "fileId", "type", "referenceId", "createdAt", "updatedAt"
        });
        sql += " WHERE image_id = ?";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, imageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Image) SqlStatementUtils.makeEntityFromResult(rs, Image.class);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean delete(Integer imageId) {
        String sql = "DELETE FROM image WHERE image_id = ?";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, imageId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean deleteByReference(String type, Integer referenceId) {
        String sql = "DELETE FROM image WHERE type = ? AND reference_id = ?";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, type);
            ps.setInt(2, referenceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }
}

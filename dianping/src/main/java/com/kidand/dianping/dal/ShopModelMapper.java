package com.kidand.dianping.dal;

import com.kidand.dianping.model.ShopModel;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ShopModelMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Sat Nov 02 08:55:42 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Sat Nov 02 08:55:42 CST 2019
     */
    int insert(ShopModel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Sat Nov 02 08:55:42 CST 2019
     */
    int insertSelective(ShopModel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Sat Nov 02 08:55:42 CST 2019
     */
    ShopModel selectByPrimaryKey(Integer id);

    List<ShopModel> selectAll();

    Integer countAllShop();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Sat Nov 02 08:55:42 CST 2019
     */
    int updateByPrimaryKeySelective(ShopModel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shop
     *
     * @mbg.generated Sat Nov 02 08:55:42 CST 2019
     */
    int updateByPrimaryKey(ShopModel record);

    List<ShopModel> recommend(@Param("longitude")BigDecimal longitude, @Param("latitude")BigDecimal latitude);
}
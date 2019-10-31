package com.kidand.dianping.dal;

import com.kidand.dianping.model.SellerModel;

import java.util.List;

public interface SellerModelMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller
     *
     * @mbg.generated Thu Oct 31 14:54:19 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller
     *
     * @mbg.generated Thu Oct 31 14:54:19 CST 2019
     */
    int insert(SellerModel record);

    List<SellerModel> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller
     *
     * @mbg.generated Thu Oct 31 14:54:19 CST 2019
     */
    int insertSelective(SellerModel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller
     *
     * @mbg.generated Thu Oct 31 14:54:19 CST 2019
     */
    SellerModel selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller
     *
     * @mbg.generated Thu Oct 31 14:54:19 CST 2019
     */
    int updateByPrimaryKeySelective(SellerModel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table seller
     *
     * @mbg.generated Thu Oct 31 14:54:19 CST 2019
     */
    int updateByPrimaryKey(SellerModel record);
}
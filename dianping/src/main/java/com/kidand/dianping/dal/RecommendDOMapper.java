package com.kidand.dianping.dal;

import com.kidand.dianping.model.RecommendDO;

public interface RecommendDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    int insert(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    int insertSelective(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    RecommendDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    int updateByPrimaryKeySelective(RecommendDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    int updateByPrimaryKey(RecommendDO record);
}
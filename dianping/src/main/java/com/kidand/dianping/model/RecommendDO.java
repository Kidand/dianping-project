package com.kidand.dianping.model;

public class RecommendDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column recommend.id
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column recommend.recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    private String recommend;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column recommend.id
     *
     * @return the value of recommend.id
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column recommend.id
     *
     * @param id the value for recommend.id
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column recommend.recommend
     *
     * @return the value of recommend.recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    public String getRecommend() {
        return recommend;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column recommend.recommend
     *
     * @param recommend the value for recommend.recommend
     *
     * @mbg.generated Sun Sep 15 15:24:33 CST 2019
     */
    public void setRecommend(String recommend) {
        this.recommend = recommend == null ? null : recommend.trim();
    }
}
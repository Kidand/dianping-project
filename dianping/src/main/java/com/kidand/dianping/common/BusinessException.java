package com.kidand.dianping.common;

public class BusinessException extends Exception {
    private CommonError commonError;

    public BusinessException(EmBusinessError emBusinessError){
        super();
        this.commonError = new CommonError(emBusinessError);
    }

    public CommonError getCommonError() {
        return commonError;
    }

    public void setCommonError(CommonError commonError) {
        this.commonError = commonError;
    }
}

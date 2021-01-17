package com.production.advangenote.models;

/**
 * @author vietnh
 * @name PasswordValidator
 * @date 1/15/21
 **/
public interface PasswordValidator {
    enum Result {
        SUCCEED, FAIL, RESTORE
    }

    void onPasswordValidated(Result result);

}

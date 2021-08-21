package com.paascloud.core.utils;
import com.paascloud.core.exception.ApiException;
import com.paascloud.core.web.api.IErrorCode;
import com.paascloud.core.web.api.ResultCode;
import org.springframework.lang.Nullable;

/**
 * api 断言
 */
public abstract class ApiAssetUtil{

    public static void state(boolean expression) {
        if (!expression) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void state(boolean expression, IErrorCode errorCode) {
        if (!expression) {
            throw new ApiException(errorCode);
        }
    }

    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void isTrue(boolean expression, IErrorCode errorCode) {
        if (!expression) {
            throw new ApiException(errorCode);
        }
    }

    public static void isNull(@Nullable Object object) {
        if (object != null) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void isNull(@Nullable Object object, IErrorCode errorCode) {
        if (object != null) {
            throw new ApiException(errorCode);
        }
    }

    public static void notNull(@Nullable Object object) {
        if (object == null) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void notNull(@Nullable Object object, IErrorCode errorCode) {
        if (object == null) {
            throw new ApiException(errorCode);
        }
    }
}

package com.x.core.utils;
import com.x.core.exception.ApiException;
import com.x.core.web.api.IErrorCode;
import com.x.core.web.api.ResultCode;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

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

    public static void notTrue(boolean expression) {
        if (expression) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void notTrue(boolean expression, IErrorCode errorCode) {
        if (expression) {
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

    public static void isStringEmpty(@Nullable String text) {
        if (!StringUtils.isEmpty(text)) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void isStringEmpty(@Nullable String text, IErrorCode errorCode) {
        if (!StringUtils.isEmpty(text)) {
            throw new ApiException(errorCode);
        }
    }

    public static void notStringEmpty(@Nullable String text) {
        if (StringUtils.isEmpty(text)) {
            throw new ApiException(ResultCode.FAILED);
        }
    }

    public static void notStringEmpty(@Nullable String text, IErrorCode errorCode) {
        if (StringUtils.isEmpty(text)) {
            throw new ApiException(errorCode);
        }
    }
}

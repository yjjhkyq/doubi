package com.x.provider.general.enums;

import com.x.core.web.api.IErrorCode;

public enum GeneralErrorEnum implements IErrorCode {
    COMMENT_REVIEW_BLOCKED(92070001L, "评论内容审核没有通过哦，请调整评论内容"),
    ;
    private long code;
    private String message;

    GeneralErrorEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

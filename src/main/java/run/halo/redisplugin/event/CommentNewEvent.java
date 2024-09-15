package com.stevenchen.redisplugin.event;

import org.springframework.context.ApplicationEvent;

public class CommentNewEvent extends ApplicationEvent {
    private final Long commentId;

    public CommentNewEvent(Object source, Long commentId) {
        super(source);
        this.commentId = commentId;
    }

    public Long getCommentId() {
        return commentId;
    }
}

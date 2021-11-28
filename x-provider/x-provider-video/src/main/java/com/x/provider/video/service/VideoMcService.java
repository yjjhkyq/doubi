package com.x.provider.video.service;

import com.x.provider.api.general.model.event.CommentEvent;
import com.x.provider.api.general.model.event.StarEvent;
import com.x.provider.api.video.model.event.VideoChangedEvent;

public interface VideoMcService {
    void onVideoChanged(VideoChangedEvent videoChangedEvent);
    void onStar(StarEvent starEvent);
    void onComment(CommentEvent commentEvent);
}

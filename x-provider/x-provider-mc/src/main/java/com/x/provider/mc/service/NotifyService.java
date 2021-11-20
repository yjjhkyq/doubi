package com.x.provider.mc.service;

import com.x.core.web.page.CursorList;
import com.x.core.web.page.CursorPageRequest;
import com.x.provider.api.mc.model.ao.SendNotifyAO;
import com.x.provider.mc.model.domain.NotifyReadBadge;
import com.x.provider.mc.model.domain.NotifySender;

import java.util.List;

public interface NotifyService {
    List<NotifySender> listNotifySender();
    CursorList listAndReadNotify(CursorPageRequest cursorPageRequest, Long senderUid, Long targetId);
    void onNotifyRead(Long senderUid, Long targetId);
    void sendNotify(SendNotifyAO notify);
    List<NotifyReadBadge> listNotifyReadBadge(Long targetId);
}

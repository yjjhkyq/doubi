package com.x.provider.mc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.x.core.utils.BeanUtil;
import com.x.core.web.page.CursorList;
import com.x.core.web.page.CursorPageRequest;
import com.x.core.web.page.TableSupport;
import com.x.provider.api.mc.enums.NotifyTargetType;
import com.x.provider.mc.mapper.NotifyMapper;
import com.x.provider.mc.mapper.NotifyReadBadgeMapper;
import com.x.provider.mc.mapper.NotifySenderMapper;
import com.x.provider.api.mc.model.ao.SendNotifyAO;
import com.x.provider.mc.model.domain.Notify;
import com.x.provider.mc.model.domain.NotifyReadBadge;
import com.x.provider.mc.model.domain.NotifySender;
import com.x.provider.mc.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    private final NotifyMapper notifyMapper;
    private final NotifySenderMapper notifySenderMapper;
    private final NotifyReadBadgeMapper notifyOutlineMapper;
    private final Executor executor;

    public NotifyServiceImpl(NotifyMapper notifyMapper,
                             NotifySenderMapper notifySenderMapper,
                             NotifyReadBadgeMapper notifyOutlineMapper,
                             @Qualifier("mcDefaultExecutor") Executor executor){
        this.notifyMapper = notifyMapper;
        this.notifySenderMapper = notifySenderMapper;
        this.notifyOutlineMapper = notifyOutlineMapper;
        this.executor = executor;
    }

    @Override
    public List<NotifySender> listNotifySender() {
        return listNotifySenderBy(null);
    }

    @Override
    public CursorList<Notify> listAndReadNotify(CursorPageRequest cursorPageRequest, Long senderUid, Long targetUid) {
        IPage page = TableSupport.buildIPageRequest(cursorPageRequest);
        LambdaQueryWrapper<Notify> query = new LambdaQueryWrapper<Notify>().eq(Notify::getSenderUid, senderUid).eq(Notify::getTargetId, targetUid).ge(Notify::getExpireDate, new Date()).gt(Notify::getId, cursorPageRequest.getCursor())
                .orderByDesc(Notify::getId);
        List<Notify> records = notifyMapper.selectPage(page, query).getRecords();
        if (CollectionUtils.isEmpty(records)){
            return new CursorList<>();
        }
        executor.execute(() -> {
            onNotifyRead(senderUid, targetUid);
        });
        return new CursorList<>(records, CollectionUtils.lastElement(records).getId());
    }

    @Override
    public void onNotifyRead(Long senderUid, Long targetUid) {
        Optional<NotifyReadBadge> notifyOutline = getNotifyReadBadgeBy(targetUid, senderUid);
        NotifySender notifySender = getNotifySender(senderUid).get();
        Long readEndNotifyId = 0L;
        if (notifySender.getTargetType().equals(NotifyTargetType.PERSONAL.getValue())){
            Optional<Notify> newestNotify = getNewestNofity(targetUid, senderUid);
            if (newestNotify.isPresent()){
                readEndNotifyId = newestNotify.get().getId();
            }
        }
        if (notifyOutline.isPresent() && (notifyOutline.get().isHasUnreadMsg() == true || !notifyOutline.get().getReadEndNotifyId().equals(readEndNotifyId))){
            notifyOutlineMapper.updateById(NotifyReadBadge.builder().id(notifyOutline.get().getId()).hasUnreadMsg(false).readEndNotifyId(readEndNotifyId).build());
        }
        else {
            notifyOutlineMapper.insert(NotifyReadBadge.builder().hasUnreadMsg(false).readEndNotifyId(readEndNotifyId).build());
        }
    }

    @Override
    public void sendNotify(SendNotifyAO saveNotifyAO) {
        NotifySender notifySender = getNotifySender(saveNotifyAO.getSenderUid()).get();
        if (notifySender.getSave()) {
            Notify notify = BeanUtil.prepare(saveNotifyAO, Notify.class);
            notify.setExpireDate(DateUtils.addDays(new Date(), notifySender.getMsgExpireDays()));
            notifyMapper.insert(notify);
        }
        if (notifySender.getTargetType().equals(NotifyTargetType.PERSONAL.getValue())) {
            Optional<NotifyReadBadge> notifyOutline = getNotifyReadBadgeBy(saveNotifyAO.getTargetId(), saveNotifyAO.getSenderUid());
            if (notifyOutline.isPresent()) {
                notifyOutlineMapper.updateById(NotifyReadBadge.builder().id(notifyOutline.get().getId()).hasUnreadMsg(true).shortMsg(saveNotifyAO.getShortMsg()).build());
            } else {
                notifyOutlineMapper.insert(NotifyReadBadge.builder().hasUnreadMsg(true).shortMsg(saveNotifyAO.getShortMsg()).senderUid(saveNotifyAO.getSenderUid()).targetUid(saveNotifyAO.getTargetId()).build());
            }
        }
    }

    @Override
    public List<NotifyReadBadge> listNotifyReadBadge(Long targetId) {
        return listNotifyReadBadgeBy(targetId, null);
    }

    private Optional<NotifyReadBadge> getNotifyReadBadgeBy(Long targetUid, Long senderUid){
        LambdaQueryWrapper<NotifyReadBadge> query = buildNotifyReadBadge(targetUid, senderUid);
        return Optional.ofNullable(notifyOutlineMapper.selectOne(query));
    }

    private List<NotifyReadBadge> listNotifyReadBadgeBy(Long targetUid, Long senderUid){
        LambdaQueryWrapper<NotifyReadBadge> query = buildNotifyReadBadge(targetUid, senderUid);
        return notifyOutlineMapper.selectList(query);
    }

    public Optional<Notify> getNewestNofity(Long targetUid, Long senderUid){
        LambdaQueryWrapper<Notify> query = buildNotifyQuery(targetUid, senderUid).orderByDesc(Notify::getId);
        return Optional.ofNullable(notifyMapper.selectOne(query));
    }

    private LambdaQueryWrapper<Notify> buildNotifyQuery(Long targetUid, Long senderUid) {
        LambdaQueryWrapper<Notify> query = new LambdaQueryWrapper<>();
        if (targetUid != null){
            query = query.eq(Notify::getTargetId, targetUid);
        }
        if (senderUid != null){
            query = query.eq(Notify::getSenderUid, senderUid);
        }
        return query;
    }

    private LambdaQueryWrapper<NotifyReadBadge> buildNotifyReadBadge(Long targetUid, Long senderUid) {
        LambdaQueryWrapper<NotifyReadBadge> query = new LambdaQueryWrapper<>();
        if (targetUid != null){
            query = query.eq(NotifyReadBadge::getTargetUid, targetUid);
        }
        if (senderUid != null){
            query = query.eq(NotifyReadBadge::getSenderUid, senderUid);
        }
        return query;
    }

    private Optional<NotifySender> getNotifySender(Long senderUid){
        return listNotifySenderBy(senderUid).stream().findFirst();
    }

    private List<NotifySender> listNotifySenderBy(Long senderUid){
        LambdaQueryWrapper<NotifySender> query = new LambdaQueryWrapper<>();
        if (senderUid != null){
            query = query.eq(NotifySender::getSenderUid, senderUid);
        }
        return notifySenderMapper.selectList(query);
    }
}

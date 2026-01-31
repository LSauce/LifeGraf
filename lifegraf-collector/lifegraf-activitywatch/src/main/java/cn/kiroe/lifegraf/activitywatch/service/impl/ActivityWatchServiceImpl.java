package cn.kiroe.lifegraf.activitywatch.service.impl;

import cn.kiroe.lifegraf.activitywatch.mapper.ActivityWatchBucketMapper;
import cn.kiroe.lifegraf.activitywatch.mapper.ActivityWatchEventMapper;
import cn.kiroe.lifegraf.activitywatch.model.ActivityWatchBucket;
import cn.kiroe.lifegraf.activitywatch.model.ActivityWatchEvent;
import cn.kiroe.lifegraf.activitywatch.remote.ActivityWatchClient;
import cn.kiroe.lifegraf.activitywatch.remote.dto.ActivityWatchBucketDTO;
import cn.kiroe.lifegraf.activitywatch.remote.dto.ActivityWatchEventDTO;
import cn.kiroe.lifegraf.activitywatch.service.ActivityWatchService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityWatchServiceImpl extends ServiceImpl<ActivityWatchEventMapper, ActivityWatchEvent> implements ActivityWatchService {

    private final ActivityWatchBucketMapper activityWatchBucketMapper;
    private final ActivityWatchEventMapper activityWatchEventMapper;
    private final ActivityWatchClient activityWatchClient;

    public ActivityWatchServiceImpl(ActivityWatchBucketMapper activityWatchBucketMapper,
                                    ActivityWatchEventMapper activityWatchEventMapper,
                                    ActivityWatchClient activityWatchClient) {
        this.activityWatchBucketMapper = activityWatchBucketMapper;
        this.activityWatchEventMapper = activityWatchEventMapper;
        this.activityWatchClient = activityWatchClient;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveActivityWatchEvent(ActivityWatchEvent event) {
        log.info("保存ActivityWatch事件: bucketId={}, startTime={}, app={}",
                event.getBucketId(), event.getStartTime(), event.getApp());
        return save(event);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveActivityWatchEvents(List<ActivityWatchEvent> events) {
        log.info("批量保存ActivityWatch事件: 数量={}", events.size());
        if (events.isEmpty()) {
            return true;
        }
        return saveBatch(events);
    }

    @Override
    public List<ActivityWatchEvent> getEventsByBucketId(Long bucketId) {
        log.info("查询bucketId={}的事件", bucketId);
        return list(Wrappers.<ActivityWatchEvent>lambdaQuery()
                .eq(ActivityWatchEvent::getBucketId, bucketId)
                .orderByAsc(ActivityWatchEvent::getStartTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveActivityWatchBucket(ActivityWatchBucket bucket) {
        log.info("保存ActivityWatch桶: name={}", bucket.getName());
        // 检查是否已存在相同的桶
        ActivityWatchBucket existingBucket = activityWatchBucketMapper.selectOne(
                Wrappers.<ActivityWatchBucket>lambdaQuery()
                        .eq(ActivityWatchBucket::getName, bucket.getName()));

        if (existingBucket != null) {
            // 更新现有桶信息
            bucket.setId(existingBucket.getId());
            return activityWatchBucketMapper.updateById(bucket) > 0;
        } else {
            // 插入新桶信息
            return activityWatchBucketMapper.insert(bucket) > 0;
        }
    }

    @Override
    public List<ActivityWatchBucket> getAllBuckets() {
        log.info("查询所有ActivityWatch桶");
        return activityWatchBucketMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public Map<String, ActivityWatchBucketDTO> syncBuckets() {
        log.info("开始同步ActivityWatch buckets");
        Map<String, ActivityWatchBucketDTO> remoteBuckets = getRemoteBuckets();

        for (Map.Entry<String, ActivityWatchBucketDTO> entry : remoteBuckets.entrySet()) {
            ActivityWatchBucketDTO remoteBucket = entry.getValue();

            // 检查本地是否已存在该bucket
            ActivityWatchBucket existingBucket = activityWatchBucketMapper.selectOne(
                Wrappers.<ActivityWatchBucket>lambdaQuery()
                    .eq(ActivityWatchBucket::getName, remoteBucket.getId())
            );

            if (existingBucket != null) {
                // 更新现有bucket信息
                existingBucket.setName(remoteBucket.getId());
                existingBucket.setType(remoteBucket.getType());
                existingBucket.setClient(remoteBucket.getClient());
                existingBucket.setHostname(remoteBucket.getHostname());
                existingBucket.setCreateTime(remoteBucket.getCreated());
                existingBucket.setUpdateTime(remoteBucket.getLastUpdated());
                activityWatchBucketMapper.updateById(existingBucket);
                log.info("更新bucket: {}", remoteBucket.getId());
            } else {
                // 创建新bucket
                ActivityWatchBucket newBucket = new ActivityWatchBucket();
                newBucket.setName(remoteBucket.getId());
                newBucket.setType(remoteBucket.getType());
                newBucket.setClient(remoteBucket.getClient());
                newBucket.setHostname(remoteBucket.getHostname());
                newBucket.setCreateTime(remoteBucket.getCreated());
                newBucket.setUpdateTime(remoteBucket.getLastUpdated());
                activityWatchBucketMapper.insert(newBucket);
                log.info("创建bucket: {}", remoteBucket.getId());
            }
        }
        log.info("同步ActivityWatch buckets 完成");
        return remoteBuckets;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncEvents(String bucketId) {
        log.info("开始同步bucket {} 的events", bucketId);

        // 获取本地该bucket最近的事件时间
        OffsetDateTime lastEventTime = activityWatchEventMapper.selectOne(
            Wrappers.<ActivityWatchEvent>lambdaQuery()
                .eq(ActivityWatchEvent::getBucketId, getBucketIdByName(bucketId))
                .orderByDesc(ActivityWatchEvent::getStartTime)
                .last("LIMIT 1")
        ) != null ? activityWatchEventMapper.selectOne(
            Wrappers.<ActivityWatchEvent>lambdaQuery()
                .eq(ActivityWatchEvent::getBucketId, getBucketIdByName(bucketId))
                .orderByDesc(ActivityWatchEvent::getStartTime)
                .last("LIMIT 1")
        ).getStartTime() : OffsetDateTime.MIN;

        // 如果没有找到任何事件，则从最小时间开始
        if (lastEventTime == OffsetDateTime.MIN) {
            // 设置一个较早的默认开始时间
            lastEventTime = OffsetDateTime.now().minusDays(365 * 10); // 10年前
        }

        // 获取当前时间作为结束时间
        OffsetDateTime endTime = OffsetDateTime.now();

        try {
            // 获取远程事件
            ActivityWatchEventDTO[] remoteEvents = getRemoteEvents(bucketId, lastEventTime, endTime);

            if (remoteEvents != null && remoteEvents.length > 0) {
                // 转换为本地模型并保存
                List<ActivityWatchEvent> eventsToSave = convertToActivityWatchEvents(remoteEvents, bucketId);

                if (!eventsToSave.isEmpty()) {
                    saveBatch(eventsToSave);
                    log.info("保存了 {} 个事件到bucket: {}", eventsToSave.size(), bucketId);
                }
            } else {
                log.info("在时间范围内没有找到新的事件: {} 到 {}", lastEventTime, endTime);
            }
        } catch (Exception e) {
            log.error("同步bucket {} 的events时发生错误", bucketId, e);
        }

        log.info("同步bucket {} 的events完成", bucketId);
    }

    @Override
    @Transactional
    public void syncAllEvents() {
        log.info("开始同步所有ActivityWatch events");

        // 先同步buckets
        Map<String, ActivityWatchBucketDTO> map = syncBuckets();
        if(map == null || map.isEmpty()) {
            log.warn("没有找到任何bucket, 或者 远程访问异常");
            return;
        }

        // 获取所有buckets
        List<ActivityWatchBucket> buckets = getAllBuckets();

        // 为每个bucket同步events
        for (ActivityWatchBucket bucket : buckets) {
            syncEvents(bucket.getName());
        }

        log.info("同步所有ActivityWatch events 完成");
    }

    @Override
    public Map<String, ActivityWatchBucketDTO> getRemoteBuckets() {
        log.info("获取远程ActivityWatch buckets");
        try {
            return activityWatchClient.getBuckets();
        } catch (Exception e) {
            log.error("获取远程ActivityWatch buckets失败", e);
            return Map.of(); // 返回空Map以避免空指针异常
        }
    }

    @Override
    public ActivityWatchEventDTO[] getRemoteEvents(String bucketId, OffsetDateTime start, OffsetDateTime end) {
        log.info("获取远程ActivityWatch events, bucketId: {}, start: {}, end: {}",
                bucketId, start, end);

        String startStr = start.toString();
        String endStr = end.toString();

        try {
            return activityWatchClient.getEvents(bucketId, startStr, endStr, "-1");
        } catch (Exception e) {
            log.error("获取远程ActivityWatch events失败, bucketId: {}", bucketId, e);
            return new ActivityWatchEventDTO[0]; // 返回空数组以避免空指针异常
        }
    }

    private Long getBucketIdByName(String bucketName) {
        ActivityWatchBucket bucket = activityWatchBucketMapper.selectOne(
            Wrappers.<ActivityWatchBucket>lambdaQuery()
                .eq(ActivityWatchBucket::getName, bucketName)
        );
        return bucket != null ? bucket.getId() : null;
    }

    private List<ActivityWatchEvent> convertToActivityWatchEvents(ActivityWatchEventDTO[] remoteEvents, String bucketName) {
        Long bucketId = getBucketIdByName(bucketName);
        if (bucketId == null) {
            log.error("无法找到bucket名称为 {} 的ID", bucketName);
            return List.of();
        }

        return java.util.Arrays.stream(remoteEvents)
            .map(dto -> {
                ActivityWatchEvent event = new ActivityWatchEvent();
                event.setBucketId(bucketId);
                event.setStartTime(dto.getTimestamp());
                event.setDuration(dto.getDuration());

                // 计算结束时间
                if (dto.getDuration() != null && dto.getDuration() > 0) {
                    event.setEndTime(dto.getTimestamp().plusSeconds(dto.getDuration().longValue()));
                } else {
                    event.setEndTime(dto.getTimestamp());
                }

                if (dto.getData() != null) {
                    event.setApp(dto.getData().getApp());
                    event.setTitle(dto.getData().getTitle());
                }

                return event;
            })
            .collect(Collectors.toList());
    }
}

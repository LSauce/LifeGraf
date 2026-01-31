package cn.kiroe.lifegraf.activitywatch.service;

import cn.kiroe.lifegraf.activitywatch.model.ActivityWatchBucket;
import cn.kiroe.lifegraf.activitywatch.model.ActivityWatchEvent;
import cn.kiroe.lifegraf.activitywatch.remote.dto.ActivityWatchBucketDTO;
import cn.kiroe.lifegraf.activitywatch.remote.dto.ActivityWatchEventDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public interface ActivityWatchService extends IService<ActivityWatchEvent> {

    /**
     * 保存ActivityWatch事件
     */
    boolean saveActivityWatchEvent(ActivityWatchEvent event);

    /**
     * 批量保存ActivityWatch事件
     */
    boolean saveActivityWatchEvents(List<ActivityWatchEvent> events);

    /**
     * 根据bucket ID查询事件
     */
    List<ActivityWatchEvent> getEventsByBucketId(Long bucketId);

    /**
     * 保存ActivityWatch桶信息
     */
    boolean saveActivityWatchBucket(ActivityWatchBucket bucket);

    /**
     * 查询所有桶信息
     */
    List<ActivityWatchBucket> getAllBuckets();

    /**
     * 同步所有buckets
     *
     * @return
     */
    Map<String, ActivityWatchBucketDTO> syncBuckets();

    /**
     * 同步指定bucket的events
     */
    void syncEvents(String bucketId);

    /**
     * 同步所有events
     */
    void syncAllEvents();

    /**
     * 获取远程buckets
     */
    Map<String, ActivityWatchBucketDTO> getRemoteBuckets();

    /**
     * 获取远程events
     */
    ActivityWatchEventDTO[] getRemoteEvents(String bucketId, OffsetDateTime start, OffsetDateTime end);
}

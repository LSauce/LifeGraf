package cn.kiroe.lifegraf.bilibili.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.kiroe.lifegraf.bilibili.BilibiliConstant;
import cn.kiroe.lifegraf.bilibili.mapper.BilibiliHistoryMapper;
import cn.kiroe.lifegraf.bilibili.model.BilibiliHistory;
import cn.kiroe.lifegraf.bilibili.remote.BilibiliApiClient;
import cn.kiroe.lifegraf.bilibili.remote.dto.BilibiliHistoryResponse;
import cn.kiroe.lifegraf.bilibili.service.BilibiliHistoryService;
import cn.kiroe.lifegraf.bilibili.service.BilibiliService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BilibiliHistoryServiceImpl implements BilibiliHistoryService {

    private final BilibiliApiClient bilibiliApiClient;
    private final BilibiliHistoryMapper bilibiliHistoryMapper;
    private final BilibiliService bilibiliService;

    public BilibiliHistoryServiceImpl(
            BilibiliApiClient bilibiliApiClient,
            BilibiliHistoryMapper bilibiliHistoryMapper,
            BilibiliService bilibiliService) {
        this.bilibiliApiClient = bilibiliApiClient;
        this.bilibiliHistoryMapper = bilibiliHistoryMapper;
        this.bilibiliService = bilibiliService;
    }

    @Override
    public void syncBilibiliHistory() {
        log.info("开始同步B站历史记录");

        try {
            String cookie = bilibiliService.getBilibiliCookie();
            if (cookie == null || cookie.isEmpty()) {
                log.warn("未找到有效的B站Cookie，无法同步历史记录");
                return;
            }
            log.debug("使用B站Cookie进行历史记录同步");

            Instant lastViewTime = bilibiliHistoryMapper.getLastViewTime();
            log.info("获取到的最后观看时间: {}", lastViewTime);

            Long viewAtTimestamp = Instant.now().toEpochMilli();
            boolean isLastPage = false;
            Long max = null;
            String business = null;

            while (!isLastPage) {
                log.info("请求B站历史记录，view_at: {}, max: {}, business: {}", viewAtTimestamp, max, business);

                ResponseEntity<BilibiliHistoryResponse> response = bilibiliApiClient.getHistoryList(
                        max, business, viewAtTimestamp, null, BilibiliConstant.HISTORY_PAGE_SIZE
                );

                if (response.getBody() == null || response.getBody().getCode() != 0) {
                    log.error("获取B站历史记录失败: {}",
                            response.getBody() != null ? response.getBody().getMessage() : "响应为空");
                    break;
                }

                BilibiliHistoryResponse.DataInfo data = response.getBody().getData();
                if (data == null || data.getList() == null || data.getList().isEmpty()) {
                    log.info("没有更多历史记录数据");
                    break;
                }

                List<BilibiliHistoryResponse.ListInfo> filteredList = filterHistoryAfter(data.getList(), lastViewTime);
                log.info("本次获取到 {} 条记录，过滤后剩余 {} 条", data.getList().size(), filteredList.size());

                List<BilibiliHistory> histories = filteredList.stream()
                        .map(this::convertToBilibiliHistory)
                        .toList();

                saveHistory(histories);

                if (filteredList.size() < data.getList().size()) {
                    log.info("已获取到所有符合条件的历史记录");
                    isLastPage = true;
                } else {
                    delayBetweenRequests();

                    if (data.getCursor() != null) {
                        max = data.getCursor().getMax();
                        business = data.getCursor().getBusiness();
                        viewAtTimestamp = data.getCursor().getViewAt();
                    } else {
                        log.info("没有更多分页数据");
                        isLastPage = true;
                    }
                }
            }

        } catch (Exception e) {
            log.error("同步B站历史记录时发生错误", e);
        }

        log.info("B站历史记录同步完成");
    }

    /**
     * 过滤出在指定时间之后的历史记录
     */
    private List<BilibiliHistoryResponse.ListInfo> filterHistoryAfter(
            List<BilibiliHistoryResponse.ListInfo> list, Instant lastViewTime) {
        long lastViewTimeSeconds = lastViewTime.toEpochMilli() / BilibiliConstant.MILLIS_PER_SECOND;
        return list.stream()
                .filter(item -> item.getViewAt() > lastViewTimeSeconds)
                .toList();
    }

    /**
     * 请求间延迟，避免API频率限制
     */
    private void delayBetweenRequests() {
        try {
            long delayMs = RandomUtil.getRandom().nextLong(
                    BilibiliConstant.MIN_DELAY_MS,
                    BilibiliConstant.MAX_DELAY_MS
            );
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            log.error("线程休眠被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 保存历史记录到数据库
     */
    private void saveHistory(List<BilibiliHistory> histories) {
        if (histories.isEmpty()) {
            log.info("没有新的B站历史记录需要同步");
            return;
        }

        List<BilibiliHistory> uniqueHistories = deduplicateHistories(histories);
        log.info("去重后共有 {} 条记录待保存", uniqueHistories.size());

        bilibiliHistoryMapper.batchInsert(uniqueHistories);
        log.info("成功同步 {} 条B站历史记录", uniqueHistories.size());
    }

    /**
     * 去重历史记录，以kid和view_at作为唯一标识
     */
    private List<BilibiliHistory> deduplicateHistories(List<BilibiliHistory> histories) {
        return histories.stream()
                .collect(Collectors.toMap(
                        history -> history.getKid() + "_" + history.getViewAt(),
                        history -> history,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .toList();
    }

    /**
     * 将API响应转换为BilibiliHistory实体
     */
    private BilibiliHistory convertToBilibiliHistory(BilibiliHistoryResponse.ListInfo listInfo) {
        BilibiliHistory history = new BilibiliHistory();

        // 基本信息
        history.setTitle(listInfo.getTitle());
        history.setLongTitle(listInfo.getLongTitle());
        history.setCover(listInfo.getCover());
        history.setUri(listInfo.getUri());

        // 历史记录信息
        BilibiliHistoryResponse.HistoryInfo historyInfo = listInfo.getHistory();
        if (historyInfo != null) {
            history.setOid(historyInfo.getOid());
            history.setEpid(historyInfo.getEpid());
            history.setBvid(historyInfo.getBvid());
            history.setPage(historyInfo.getPage());
            history.setCid(historyInfo.getCid());
            history.setPart(historyInfo.getPart());
            history.setBusiness(historyInfo.getBusiness());
            history.setDt(historyInfo.getDt());
        }

        // 作者和视频信息
        history.setVideos(listInfo.getVideos());
        history.setAuthorName(listInfo.getAuthorName());
        history.setAuthorFace(listInfo.getAuthorFace());
        history.setAuthorMid(listInfo.getAuthorMid());

        // 观看信息
        history.setViewAt(Instant.ofEpochSecond(listInfo.getViewAt()));
        history.setProgress(listInfo.getProgress());
        history.setBadge(listInfo.getBadge());
        history.setShowTitle(listInfo.getShowTitle());
        history.setDuration(listInfo.getDuration());
        history.setCurrentVal(listInfo.getCurrent());
        history.setTotal(listInfo.getTotal());
        history.setNewDesc(listInfo.getNewDesc());
        history.setIsFinish(listInfo.getIsFinish());
        history.setIsFav(listInfo.getIsFav());

        // 其他信息
        history.setKid(listInfo.getKid());
        history.setTagName(listInfo.getTagName());
        history.setLiveStatus(listInfo.getLiveStatus());

        // 时间戳
        Instant now = Instant.now();
        history.setCreateTime(now);
        history.setUpdateTime(now);

        return history;
    }
}

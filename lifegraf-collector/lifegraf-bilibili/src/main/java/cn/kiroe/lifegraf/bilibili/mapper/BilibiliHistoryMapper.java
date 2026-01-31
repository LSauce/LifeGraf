package cn.kiroe.lifegraf.bilibili.mapper;

import cn.kiroe.lifegraf.bilibili.model.BilibiliHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

@Mapper
public interface BilibiliHistoryMapper extends BaseMapper<BilibiliHistory> {

    /**
     * 获取最后观看时间
     * @return 最后观看时间，如果不存在则返回最小时间
     */
    Instant getLastViewTime();

    /**
     * 批量插入B站历史记录
     * @param histories 历史记录列表
     */
    void batchInsert(@Param("histories") List<BilibiliHistory> histories);
}

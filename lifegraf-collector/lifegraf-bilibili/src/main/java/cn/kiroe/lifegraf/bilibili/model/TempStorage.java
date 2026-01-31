package cn.kiroe.lifegraf.bilibili.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("temp_storage")
public class TempStorage {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("key_name")
    private String keyName;

    @TableField("value")
    private String value;

    @TableField("expire_time")
    private Instant expireTime;

    @TableField("create_time")
    private Instant createTime;

    @TableField("update_time")
    private Instant updateTime;
}

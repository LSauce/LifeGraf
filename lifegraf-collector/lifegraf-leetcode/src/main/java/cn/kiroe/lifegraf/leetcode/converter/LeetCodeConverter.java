package cn.kiroe.lifegraf.leetcode.converter;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.kiroe.lifegraf.leetcode.model.LeetCodeSubmission;
import cn.kiroe.lifegraf.leetcode.remote.dto.LeetCodeSubmissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LeetCodeConverter {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userSlug", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "originalJson", ignore = true)
    @Mapping(source = "submissionId", target = "submissionId")
    @Mapping(source = "submitTime", target = "submitTime", qualifiedByName = "timestampToInstant")
    @Mapping(source = "question.title", target = "questionTitle")
    @Mapping(source = "question.translatedTitle", target = "questionTranslatedTitle")
    @Mapping(source = "question.titleSlug", target = "titleSlug")
    @Mapping(source = "question.questionFrontendId", target = "questionFrontendId")
    LeetCodeSubmission dtoToPo(LeetCodeSubmissionResponse.RecentACSubmission dto);

    List<LeetCodeSubmission> dtosToPos(List<LeetCodeSubmissionResponse.RecentACSubmission> dtos);

    @Named("timestampToInstant")
    default Instant fromTimestamp(Long timestamp) {
        return Instant.ofEpochSecond(timestamp);
    }
}

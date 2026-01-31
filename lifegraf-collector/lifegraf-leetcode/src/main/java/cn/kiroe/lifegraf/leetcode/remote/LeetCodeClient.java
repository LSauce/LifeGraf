package cn.kiroe.lifegraf.leetcode.remote;

import cn.kiroe.lifegraf.leetcode.remote.dto.LeetCodeFollowerResponse;
import cn.kiroe.lifegraf.leetcode.remote.dto.LeetCodeSubmissionResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface LeetCodeClient {

    @PostExchange("/graphql/noj-go/")
    LeetCodeSubmissionResponse getRecentACSubmissions(@RequestBody GraphQLRequest request);

    @PostExchange("/graphql/noty")
    LeetCodeFollowerResponse getFollowers(@RequestBody GraphQLRequest request);

    record GraphQLRequest(String query, Object variables) {
    }
}

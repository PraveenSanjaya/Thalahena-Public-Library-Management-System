package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsDTO {
    private Long totalUsers;
    private Long staffCount;
    private Long adminCount;
    private Long activeMembers;
    private Long inactiveMembers;
    private Map<String, Long> genderDistribution;
    private Map<String, Long> ageDistribution;
    private List<FeedbackDTO> recentFeedback;
}

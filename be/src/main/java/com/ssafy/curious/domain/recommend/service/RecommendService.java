package com.ssafy.curious.domain.recommend.service;

import com.ssafy.curious.domain.article.entity.ArticleInfoEntity;
import com.ssafy.curious.domain.article.repository.ArticleInfoRepository;
import com.ssafy.curious.domain.member.entity.MemberEntity;
import com.ssafy.curious.domain.member.repository.MemberRepository;
import com.ssafy.curious.domain.model.ArticleCategory;
import com.ssafy.curious.domain.model.ArticlePress;
import com.ssafy.curious.domain.model.ArticleMetadata;
import com.ssafy.curious.domain.recommend.entity.RecommendPoolCFEntity;
import com.ssafy.curious.domain.recommend.entity.RecommendPoolClusterEntity;
import com.ssafy.curious.domain.model.RecommendScore;
import com.ssafy.curious.domain.recommend.repository.RecommendPooClusterRepository;
import com.ssafy.curious.domain.recommend.repository.RecommendPoolCFRepository;
import com.ssafy.curious.global.utils.ArticleCategoryConverter;
import com.ssafy.curious.global.utils.ArticlePressConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendPooClusterRepository recommendPooClusterRepository;
    private final RecommendPoolCFRepository recommendPoolCFRepository;
    private final MemberRepository memberRepository;
    private final ArticleInfoRepository articleInfoRepository;

    LocalDate today = LocalDate.now();
    LocalDateTime startTime = today.atStartOfDay();
    LocalDateTime endTime = today.atTime(23, 59, 59);

    /**
     * 클러스터링 기반 추천 기사 후보군에서 추천 점수를 계산하여 추천 기사 ID 리스트 리턴
     */
    // Todo: 추천 점수 weight 조정, 기사 리턴 개수 구체화
    public List<ArticleInfoEntity> recommendClusterArticle(Long memberId) {
        // 추천 기사 후보군 가져오기
        RecommendPoolClusterEntity recommendPoolCluster = recommendPooClusterRepository.findByCreatedAtBetween(startTime, endTime);
        List<ArticleMetadata> articleList = recommendPoolCluster.getArticleList();
        log.info("poolCluster {}", articleList.toString());

        // 멤버의 카테고리, 언론사 선호도 가져오기
        Optional<MemberEntity> member = memberRepository.findById(memberId);
        Map<ArticleCategory, Integer> categoryPreference = member.get().getCategoryPreference();
        Map<ArticlePress, Integer> pressPreference = member.get().getPressPreference();

        // 각 카테고리 별로 가장 높은 점수를 저장하기 위한 맵
        Map<ArticleCategory, RecommendScore> bestScoresByCategory = new HashMap<>();

        for (ArticleMetadata article : articleList) {
            Integer categoryScore = categoryPreference.getOrDefault(ArticleCategoryConverter.convertKrToEnumCategory(article.getCategory1()), -100);
            Integer pressScore = pressPreference.getOrDefault(ArticlePressConverter.convertKrToEnumPress(article.getCompany()), -100);
            Integer totalScore = categoryScore + pressScore;

            RecommendScore currentScore = bestScoresByCategory.get(article.getCategory1());

            // 현재 카테고리의 최고 점수가 없거나 현재 점수가 더 높을 때 업데이트
            if (currentScore == null || totalScore > currentScore.getScore()) {
                bestScoresByCategory.put(ArticleCategoryConverter.convertKrToEnumCategory(article.getCategory1()), new RecommendScore(article.getOriginal_url(), totalScore));
            }
        }

        // 추천 점수 기준 정렬
        List<RecommendScore> bestArticleList = new ArrayList<>(bestScoresByCategory.values());
        log.info("bestClusterArticleList {}", bestArticleList.toString());

        // 상위 5개 기사 추출
        List<ArticleInfoEntity> topArticles = new ArrayList<>();
        int count = 0;
        for (RecommendScore score : bestArticleList) {
            if (count < 5) {
                Optional<ArticleInfoEntity> articleInfo = articleInfoRepository.findByOriginalUrl(score.getArticleUrl());
                // 조회한 기사가 존재할 때만 추가
                articleInfo.ifPresent(articleInfoEntity -> {
                    topArticles.add(articleInfoEntity);
                });
                log.info("cluster article urls {}", score.getArticleUrl());
                count++;
            } else {
                break;
            }
        }
        log.info("topClusterArticles {}", topArticles.toString());

        return topArticles;
    }

    /**
     * 협업 필터링 기반 추천 기사 후보군에서 추천 점수를 계산하여 추천 기사 ID 리스트 리턴
     */
    // Todo: 추천 점수 weight 조정, 기사 리턴 개수 구체화
    public List<ArticleInfoEntity> recommendCFArticle(Long memberId) {
        // 추천 기사 후보군 가져오기
        RecommendPoolCFEntity recommendPoolCF = recommendPoolCFRepository.findByCreatedAtBetweenAndMemberId(startTime, endTime, memberId);
        List<String> articleUrlList = recommendPoolCF.getArticleList();
        log.info("articleUrlList {}", articleUrlList.toString());

        // 상위 5개 기사 추출
        List<ArticleInfoEntity> topArticles = new ArrayList<>();
        int count = 0;
        for (String articleUrl : articleUrlList) {
            if (count < 5) {
                Optional<ArticleInfoEntity> articleInfo = articleInfoRepository.findByOriginalUrl(articleUrl);
                // 조회한 기사가 존재할 때만 추가
                articleInfo.ifPresent(articleInfoEntity -> {
                    topArticles.add(articleInfoEntity);
                });
                count++;
            } else {
                break;
            }
        }
        log.info("topCFArticles {}", topArticles.toString());

        return topArticles;
    }
}

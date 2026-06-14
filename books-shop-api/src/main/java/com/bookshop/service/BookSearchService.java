package com.bookshop.service;

import com.bookshop.config.ElasticsearchConfig;
import com.bookshop.dto.BookDocument;
import com.bookshop.entity.Book;
import com.bookshop.entity.User;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ES 图书搜索服务 —— 用 RestTemplate 直接调 ES REST API。
 */
@Slf4j
@Service
public class BookSearchService {

    private static final String INDEX_URL = ElasticsearchConfig.ES_URL + "/books";
    private final RestTemplate esRestTemplate;
    private final ObjectMapper objectMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public BookSearchService(RestTemplate esRestTemplate,
                             BookMapper bookMapper, UserMapper userMapper) {
        this.esRestTemplate = esRestTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    /**
     * 启动时创建 ES 索引并同步全部已有图书。
     */
    @PostConstruct
    public void init() {
        try {
            esRestTemplate.getForObject(INDEX_URL, String.class);
            log.info("ES 索引已存在，跳过全量同步");
            return;
        } catch (Exception e) {
            // 索引不存在，继续创建
        }

        // 创建索引（单节点：1 分片 0 副本）
        try {
            Map<String, Object> settings = new HashMap<>();
            settings.put("number_of_shards", 1);
            settings.put("number_of_replicas", 0);
            Map<String, Object> indexBody = new HashMap<>();
            indexBody.put("settings", settings);
            esRestTemplate.put(INDEX_URL, indexBody);
            log.info("ES 索引创建成功");
            // 等 ES 准备好
            Thread.sleep(2000);
        } catch (Exception ex) {
            log.error("ES 索引创建失败", ex);
            return;
        }

        // 首次全量同步
        fullSync();
    }

    private void fullSync() {
        try {
            List<Book> allBooks = bookMapper.selectList(null);
            log.info("开始 ES 全量同步，共 {} 条", allBooks.size());
            int count = 0;
            for (Book book : allBooks) {
                User user = book.getUploaderId() != null
                        ? userMapper.selectById(book.getUploaderId()) : null;
                String uploaderName = book.getUploaderId() == null
                        ? "管理员" : (user != null ? user.getUsername() : "用户");
                sync(new BookDocument(book, uploaderName));
                count++;
            }
            log.info("ES 全量同步完成，成功 {} 条", count);
        } catch (Exception e) {
            log.error("ES 全量同步失败", e);
        }
    }

    /**
     * PUT /books/_doc/{id}
     */
    public void sync(BookDocument doc) {
        try {
            Map<String, Object> body = objectMapper.convertValue(doc, Map.class);
            esRestTemplate.put(INDEX_URL + "/_doc/" + doc.getId(), body);
            log.info("ES 同步成功: bookId={}", doc.getId());
        } catch (Exception e) {
            log.error("ES 同步失败: bookId={}", doc.getId(), e);
        }
    }

    /**
     * POST /books/_search
     */
    public List<BookDocument> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            Map<String, Object> multiMatch = new HashMap<>();
            multiMatch.put("fields", Arrays.asList("bookName", "bookAuthor", "bookSn"));
            multiMatch.put("query", keyword);

            Map<String, Object> query = new HashMap<>();
            query.put("multi_match", multiMatch);

            Map<String, Object> body = new HashMap<>();
            body.put("query", query);
            body.put("size", 50);

            String response = esRestTemplate.postForObject(
                    INDEX_URL + "/_search", body, String.class);
            return parseHits(response);
        } catch (Exception e) {
            log.error("ES 搜索失败: keyword={}", keyword, e);
            return Collections.emptyList();
        }
    }

    /**
     * DELETE /books/_doc/{id}
     */
    public void remove(Integer bookId) {
        try {
            esRestTemplate.delete(INDEX_URL + "/_doc/" + bookId);
            log.info("ES 删除成功: bookId={}", bookId);
        } catch (Exception e) {
            log.error("ES 删除失败: bookId={}", bookId, e);
        }
    }

    private List<BookDocument> parseHits(String response) {
        try {
            JsonNode hits = objectMapper.readTree(response)
                    .get("hits").get("hits");
            List<BookDocument> docs = new ArrayList<>();
            for (JsonNode hit : hits) {
                JsonNode source = hit.get("_source");
                BookDocument doc = objectMapper.treeToValue(source, BookDocument.class);
                docs.add(doc);
            }
            return docs;
        } catch (Exception e) {
            log.error("ES 结果解析失败", e);
            return Collections.emptyList();
        }
    }
}

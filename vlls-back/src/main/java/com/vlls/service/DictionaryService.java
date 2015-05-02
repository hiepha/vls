package com.vlls.service;

import com.vlls.web.model.DictItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hiephn on 2014/10/03.
 */
@Service
public class DictionaryService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryService.class);

    protected List<DictItemResponse> parseWordContent(String name, String wordContent, Boolean hasAudio) {
        DictItemReader dictItemReader = new DictItemReader(name, wordContent, hasAudio);
        return dictItemReader.getItems();
    }

    /**
     * <span class="title">table /"teibl/</span>
     *
     * <span class="type">danh từ</span>
     *   <ul>
     *       <li>cái bàn</li>
     *       <li>bàn ăn
     *           <!-- Redundant example -->
     *           <ul><li><a></a></li></ul>
     *           <!-- Redundant example ends -->
     *       </li>
     *   </ul>
     *
     * <!-- Redundant Idioms -->
     * <span class="type">Idioms</span>
     *   <!-- Not well-formed -->
     *   <ul>
     *       <li></li>
     *   </ul>
     *   <!-- Not well-formed ends -->
     * <!-- Redundant Idioms ends -->
     *
     * <span class="type">[động|tính|phó|...] từ</span>
     *   <ul>
     *       <li></li>
     *       <li>
     *           <!-- Redundant example -->
     *           <ul><li><a></a></li></ul>
     *           <!-- Redundant example ends -->
     *       </li>
     *   </ul>
     *
     */
    class DictItemReader {
        private List<DictItemResponse> items;
        private String name;
        private String pronun;

        DictItemReader(String name, String content, Boolean hasAudio) {
            this.name = name;

            List<DictSpan> spanList = new ArrayList<>();
            int position = 0;
            boolean isFirstSpan = true;
            while (position >= 0) {
                int startIdx = indexOf(content, "<span class=\"t", position);
                int endIdx = indexOf(content, "<span class=\"t", startIdx + "<span class=\"t".length());
                position = endIdx;
                String spanString;
                if (endIdx >= 0) {
                    spanString = substring(content, startIdx, endIdx);
                } else {
                    spanString = substring(content, startIdx);
                }
                if (!startsWithIgnoreCase(spanString, "<span class=\"type\">Idioms</span>")) {
                    DictSpan span = new DictSpan(spanString);
                    if (isFirstSpan) {
                        this.pronun = span.getPronun();
                        isFirstSpan = false;
                    }
                    spanList.add(span);
                }
            }

            this.items = new ArrayList<>(spanList.size());
            spanList.forEach(span -> {
                if (span.getUl().getMeanings() != null) {
                    for (String meaning : span.getUl().getMeanings()) {
                        DictItemResponse itemResponse = new DictItemResponse();
                        itemResponse.setName(this.name);
                        itemResponse.setMeaning(meaning);
                        itemResponse.setType(span.getSpanValue());
                        itemResponse.setPronun(this.pronun);
                        itemResponse.setHasAudio(hasAudio);
                        this.items.add(itemResponse);
                    }
                }
            });
        }

        public List<DictItemResponse> getItems() {
            return items;
        }

        public String getName() {
            return name;
        }
    }

    class DictSpan {
        private String classAttr;
        private DictUl ul;
        private String pronun;
        private String spanValue;

        /**
         *
         * @param content <span ...>...</span>
         */
        DictSpan(String content) {
            // Get class
            int startIdx = indexOf(content, "class=\"") + "class=\"".length();
            int endIdx = indexOf(content, "\"", startIdx);
            this.classAttr = substring(content, startIdx, endIdx);

            // Get span value
            startIdx = indexOf(content, ">") + 1;
            endIdx = indexOf(content, "</");
            this.spanValue = substring(content, startIdx, endIdx);

            // Get pronun
            startIdx = indexOf(spanValue, "/");
            if (startIdx > 0) {
                endIdx = indexOf(spanValue, "/", startIdx + 1) + 1;
                this.pronun = substring(spanValue, startIdx, endIdx);
            }

            // Get meaning ul
            String ulString = substring(content, indexOf(content, "<ul>"));
            if (isNotEmpty(ulString)) {
                this.ul = new DictUl(ulString);
            }
        }

        public String getClassAttr() {
            return classAttr;
        }

        public DictUl getUl() {
            return ul;
        }

        public String getPronun() {
            return pronun;
        }

        public String getSpanValue() {
            return spanValue;
        }
    }

    class DictUl {
        private String[] meanings;

        /**
         *
         * @param content <ul>...</ul>
         */
        DictUl(String content) {
            String cleanContent = removeInsideUl(
                    substring(content, "<ul>".length(), content.length() - "</ul>".length()));
            this.meanings = substringsBetween(cleanContent, "<li>", "</li>");
        }

        private String removeInsideUl(String content) {
            if (contains(content, "<ul>")) {
                int position = 0;
                StringBuilder contentBuilder = new StringBuilder();
                int ulLastIdx = lastIndexOf(content, "</ul>") + "</ul>".length();

                while (position < ulLastIdx) {
                    int ulStartIdx = indexOf(content, "<ul>", position);
                    contentBuilder.append(substring(content, position, ulStartIdx));
                    position = indexOf(content, "</ul>", position) + "</ul>".length();
                }
                contentBuilder.append(substring(content, position));
                return contentBuilder.toString();
            } else {
                return content;
            }
        }

        public String[] getMeanings() {
            return meanings;
        }
    }
}

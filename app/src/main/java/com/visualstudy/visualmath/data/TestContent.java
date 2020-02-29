package com.visualstudy.visualmath.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestContent {

    /**
     * An array of sample (dummy) items.
     */
    private List<TestItem> ITEMS = new ArrayList<TestItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    private   Map<String, TestItem> ITEM_MAP = new HashMap<String, TestItem>();

    public List<TestItem> getITEMS() {
        return ITEMS;
    }

    public void setITEMS(List<TestItem> ITEMS) {
        this.ITEMS = ITEMS;
    }

    public Map<String, TestItem> getITEM_MAP() {
        return ITEM_MAP;
    }

    public void setITEM_MAP(Map<String, TestItem> ITEM_MAP) {
        this.ITEM_MAP = ITEM_MAP;
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TestItem {
        //public final String probID;//게시글 고유 id
        private   String id;
        private  String content;
        private  String details;

        public TestItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}


package com.example.schedulemedical.model.dto.response;

import com.example.schedulemedical.model.Notification;
import java.util.List;

public class NotificationResponse {
    private List<Notification> data;
    private Meta meta;

    public static class Meta {
        private int total;
        private int page;
        private int limit;
        private int totalPages;

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }

    public List<Notification> getData() { return data; }
    public void setData(List<Notification> data) { this.data = data; }
    public Meta getMeta() { return meta; }
    public void setMeta(Meta meta) { this.meta = meta; }
} 
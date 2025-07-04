package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DoctorListResponse {

    @SerializedName("data")
    private List<DoctorResponse> data;

    @SerializedName("message")
    private String message;

    @SerializedName("code")
    private Integer code;

    @SerializedName("meta")
    private DoctorListResponse.PaginationMeta meta;

    public DoctorListResponse() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public List<DoctorResponse> getData() { return data; }
    public void setData(List<DoctorResponse> data) { this.data = data; }

    public DoctorListResponse.PaginationMeta getMeta() { return meta; }
    public void setMeta(DoctorListResponse.PaginationMeta meta) { this.meta = meta; }

    public static class PaginationMeta {
        @SerializedName("page")
        private Integer page;
        @SerializedName("limit")
        private Integer limit;
        @SerializedName("total")
        private Integer total;
        @SerializedName("totalPages")
        private Integer totalPages;
        @SerializedName("hasNext")
        private Boolean hasNext;
        @SerializedName("hasPrev")
        private Boolean hasPrev;

        public PaginationMeta() {
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public Boolean getHasNext() {
            return hasNext;
        }

        public void setHasNext(Boolean hasNext) {
            this.hasNext = hasNext;
        }

        public Boolean getHasPrev() {
            return hasPrev;
        }

        public void setHasPrev(Boolean hasPrev) {
            this.hasPrev = hasPrev;
        }
    }
}

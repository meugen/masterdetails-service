package meugeninua.masterdetails.caching;

public interface CachingConstants {

    String CACHE_DETAILS_LIST = "details-list";
    String CACHE_DETAIL_BY_ID = "detail-by-id";
    String CACHE_MASTERS_LIST = "masters-list";
    String CACHE_MASTER_BY_ID = "master-by-id";

    default String mastersListKey() {
        return String.format("%s::", CACHE_MASTERS_LIST);
    }

    default String masterByIdKey(Long masterId) {
        return String.format("%s::%d", CACHE_MASTER_BY_ID, masterId);
    }

    default String detailsListKey(Long masterId) {
        return String.format("%s::%d", CACHE_DETAILS_LIST, masterId);
    }

    default String detailByIdKey(Long masterId, Long detailId) {
        return String.format("%s::%d/%d", CACHE_DETAIL_BY_ID, masterId, detailId);
    }
}

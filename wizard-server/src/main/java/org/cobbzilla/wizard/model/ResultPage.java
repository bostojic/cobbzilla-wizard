package org.cobbzilla.wizard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.cobbzilla.util.string.StringUtil;
import org.cobbzilla.wizard.validation.ValidEnum;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor @Accessors(chain=true)
public class ResultPage {

    public static final String PARAM_USE_PAGINATION = "page";
    public static final String PARAM_PAGE_NUMBER    = "pn";
    public static final String PARAM_PAGE_SIZE      = "ps";
    public static final String PARAM_SORT_FIELD     = "sf";
    public static final String PARAM_SORT_ORDER     = "so";
    public static final String PARAM_FILTER         = "q";
    public static final String PARAM_BOUNDS         = "b";

    public static final int MAX_FILTER_LENGTH = 50;
    public static final int MAX_SORTFIELD_LENGTH = 50;
    public static final String DEFAULT_SORT_FIELD = "ctime";

    public enum SortOrder { ASC, DESC }
    public static final String DEFAULT_SORT = SortOrder.DESC.name();

    public static final ResultPage DEFAULT_PAGE = new ResultPage();
    public static final ResultPage FIRST_RESULT = new ResultPage(1, 1);

    public static final ResultPage LARGE_PAGE = new ResultPage(1, 100);

    public ResultPage(Integer pageNumber, Integer pageSize, String sortField, String sortOrder, String filter, Map<String, String> bounds) {
        if (pageNumber != null) this.pageNumber = pageNumber;
        if (pageSize != null) this.pageSize = pageSize;
        if (sortField != null) this.sortField = sortField;
        if (sortOrder != null) this.sortOrder = SortOrder.valueOf(sortOrder).name();
        if (filter != null) this.filter = filter;
        this.bounds = bounds;
    }

    public ResultPage (int pageNumber, int pageSize) {
        this(pageNumber, pageSize, null, normalizeSortOrder(null), null);
    }

    public ResultPage (int pageNumber, int pageSize, String sortField, SortOrder sortOrder) {
        this(pageNumber, pageSize, sortField, sortOrder, null);
    }

    public ResultPage (int pageNumber, int pageSize, String sortField, SortOrder sortOrder, String filter) {
        this(pageNumber, pageSize, sortField, normalizeSortOrder(sortOrder), filter, null);
    }

    public ResultPage (int pageNumber, int pageSize, String sortField, String sortOrder, String filter) {
        this(pageNumber, pageSize, sortField, sortOrder, filter, null);
    }

    public ResultPage (int pageNumber, int pageSize, String sortField, SortOrder sortOrder, String filter, Map<String, String> bounds) {
        this(pageNumber, pageSize, sortField, normalizeSortOrder(sortOrder), filter, bounds);
    }

    private static String normalizeSortOrder(SortOrder sortOrder) {
        return (sortOrder == null) ? ResultPage.DEFAULT_SORT : sortOrder.name();
    }

    public static ResultPage singleResult (String sortField, SortOrder sortOrder) {
        return new ResultPage(1, 1, sortField, sortOrder);
    }

    public static ResultPage singleResult (String sortField, SortOrder sortOrder, String filter) {
        return new ResultPage(new Integer(1), new Integer(1), sortField, sortOrder, filter);
    }

    @Getter @Setter private int pageNumber = 1;

    @JsonIgnore public int getPageOffset () { return (pageNumber-1) * pageSize; }

    @Getter @Setter private int pageSize = 10;

    @Setter private String sortField = DEFAULT_SORT_FIELD;
    public String getSortField() {
        // only return the first several chars, to thwart a hypothetical injection attack.
        final String sort = StringUtil.empty(sortField) ? DEFAULT_SORT_FIELD : sortField;
        return StringUtil.prefix(sort, MAX_SORTFIELD_LENGTH);
    }
    @JsonIgnore public boolean getHasSortField () { return sortField != null; }

    @ValidEnum(type=SortOrder.class, emptyOk=true, message=BasicConstraintConstants.ERR_SORT_ORDER_INVALID)
    @Getter @Setter private String sortOrder = ResultPage.DEFAULT_SORT;

    public ResultPage setSortOrder(SortOrder order) { sortOrder = order.name(); return this; }


    @JsonIgnore public SortOrder getSortType () { return sortOrder == null ? null : SortOrder.valueOf(sortOrder); }

    @Setter private String filter = null;
    public String getFilter() {
        // only return the first several chars, to thwart a hypothetical injection attack.
        return StringUtil.prefix(filter, MAX_FILTER_LENGTH);
    }
    @JsonIgnore public boolean getHasFilter() { return filter != null && filter.trim().length() > 0; }

    @Getter @Setter private Map<String, String> bounds;
    @JsonIgnore public boolean getHasBounds() { return bounds != null && !bounds.isEmpty(); }

    public void addBound(String name, String value) {
        if (bounds == null) bounds = new LinkedHashMap<>();
        bounds.put(name, value);
    }

}

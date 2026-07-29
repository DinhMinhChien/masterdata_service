package vn.com.ssv.master_data.common.persistence;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import vn.com.ssv.master_data.common.exception.ResourceNotFoundException;
import vn.com.ssv.master_data.common.persistence.dto.PageDto;
import vn.com.ssv.master_data.common.util.Const;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

// Hỗ trợ truy vấn native query động, phức tạp
// Chỉ truyền giá trị qua named param (:key) hoặc whitelist (buildSafeOrder);
// Không nối chuỗi giá trị từ người dùng vào câu SQL tránh sql injection
@Slf4j
@Repository
public class NativeQuerySupport {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private static final AtomicLong KEY_SEQ = new AtomicLong();

    // Sinh điều kiện "AND field = ANY(:key)" cho list ) + nạp giá trị vào params. List rỗng -> trả "".
    public static <T> String genSqlWhereIn(String field, List<T> values, Map<String, Object> params) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        String key = "param_" + field + "_" + KEY_SEQ.incrementAndGet(); // key duy nhất, không trùng
        params.put(key, values);
        return " AND " + field + " = ANY(:" + key + ")";
    }

    // Gọi stored procedure theo tên + tham số.
    public Map<String, Object> callProcedure(String procedureName, SqlParameterSource params) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
        return call.execute(params);
    }

    // Dựng PageRequest từ PageDto: áp default page/size, chặn size vượt SIZE_MAX (bảo vệ DB), thêm sort nếu có.
    public PageRequest getPage(PageDto pageDto) {
        int page = pageDto.getPage() == null ? Const.PAGE_DEFAULT : pageDto.getPage();
        int size = pageDto.getSize() == null ? Const.SIZE_DEFAULT : pageDto.getSize();
        if (size > Const.SIZE_MAX) {
            size = Const.SIZE_MAX;
        }
        return PageRequest.of(page, size);
    }

    public String appendLikeExpressionById(String value) {
        return "%," + value.trim() + ",%";
    }

    public String appendLikeExpression(String value) {
        return "%" + value.trim() + "%";
    }

    // Bọc giá trị cho LIKE + escape ký tự đặc biệt (% _ ! [ ]) để không bị hiểu là wildcard.
    public String appendLikeEscapee(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.replaceAll("([%_!\\[\\]])", "\\\\$1");
        }
        return "%" + value.trim() + "%";
    }

    public <T> T getFirstData(String sql, Map<String, Object> params, Class<T> clazz) {
        List<T> result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
        if (result.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return result.getFirst();
    }

    // Query trả về list (map sang class).
    public <T> List<T> getList(String sql, Map<String, Object> params, Class<T> clazz) {
        log.info("[getList] params={}", params);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
    }

    // Lấy tên các field của class -> dùng làm WHITELIST cột cho ORDER BY.
    public static Set<String> getAllowedColumns(Class<?> clazz) {
        Set<String> fields = new HashSet<>();
        while (clazz != null && clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                fields.add(f.getName());
            }
            clazz = clazz.getSuperclass(); // đi lên class cha
        }
        return fields;
    }

    // Utility chuyển camelCase sang snake_case
    public static String camelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }


    // Chỉ chấp nhận cột nằm trong whitelist + hướng ASC/DESC -> chống SQL-injection ở sort.
    public static String buildSafeOrder(String sort, String direction, List<String> sorts, Class<?> clazz) {
        Set<String> allowed = getAllowedColumns(clazz);
        List<String> orderClauses = new ArrayList<>();

        // Ưu tiên multi-sort
        if (sorts != null && !sorts.isEmpty()) {
            for (String s : sorts) {
                String[] parts = s.split(",");
                if (parts.length == 2) {
                    String col = parts[0].trim();
                    String dir = parts[1].trim();
                    if (allowed.contains(col) &&
                            ("ASC".equalsIgnoreCase(dir) || "DESC".equalsIgnoreCase(dir))) {
                        orderClauses.add(camelToSnake(col) + " " + dir.toUpperCase());
                    }
                }
            }
        }

        // Fallback single sort
        if (orderClauses.isEmpty() && StringUtils.isNotBlank(sort) && allowed.contains(sort)
                && ("ASC".equalsIgnoreCase(direction) || "DESC".equalsIgnoreCase(direction))) {
            orderClauses.add(camelToSnake(sort) + " " + direction.toUpperCase());
        }

        return orderClauses.isEmpty() ? null : String.join(", ", orderClauses);
    }


    // Phân trang native
    public <T> Page<T> getListPagination(String sql, Map<String, Object> params, PageDto pageDto, Class<T> clazz) {
        PageRequest pageable = getPage(pageDto);
        String orderBy = buildSafeOrder(pageDto.getSort(), pageDto.getDirection(), pageDto.getSorts(), clazz);

        String pagedSql = sql;
        if (StringUtils.isNotBlank(orderBy)) {
            pagedSql += " ORDER BY " + orderBy;
        }
        // LIMIT/OFFSET dùng %d (số nguyên) -> an toàn injection
        pagedSql += String.format(" LIMIT %d OFFSET %d", pageable.getPageSize(), pageable.getOffset());

        long total = getTotalRow(sql, params);
        List<T> rows = namedParameterJdbcTemplate.query(pagedSql, params, new BeanPropertyRowMapper<>(clazz));
        return new PageImpl<>(rows, pageable, total);
    }

    // Query list với MapSqlParameterSource.
    public <T> List<T> getResultList(String sql, MapSqlParameterSource params, Class<T> clazz) {
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
    }

    // Lấy bản ghi đầu (MapSqlParameterSource); không có -> 404.
    public <T> T getFirstResult(String sql, MapSqlParameterSource params, Class<T> clazz) {
        List<T> result = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
        if (result.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return result.getFirst();
    }

    // Đếm tổng số dòng của 1 câu query
    public Long getTotalRow(String sql, Map<String, Object> params) {
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS sub";
        return namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);
    }

    // UPDATE/DELETE/INSERT native -> trả số dòng bị ảnh hưởng.
    public int updateList(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.update(sql, params);
    }


}

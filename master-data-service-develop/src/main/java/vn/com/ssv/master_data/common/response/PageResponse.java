package vn.com.ssv.master_data.common.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {

    List<T> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean last;

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())            // danh sách bản ghi trang hiện tại
                .page(page.getNumber())                // chỉ số trang (0-based)
                .size(page.getSize())                  // kích thước trang
                .totalElements(page.getTotalElements()) // tổng số bản ghi
                .totalPages(page.getTotalPages())       // tổng số trang
                .last(page.isLast())                    // có phải trang cuối không
                .build();
    }
}

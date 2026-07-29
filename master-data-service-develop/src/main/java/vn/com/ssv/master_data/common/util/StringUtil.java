package vn.com.ssv.master_data.common.util;

import lombok.experimental.UtilityClass;

import java.text.Normalizer;
import java.time.LocalDateTime;

import static vn.com.ssv.master_data.common.util.Const.FILE_TIME_FORMAT;

@UtilityClass
public final class StringUtil {
    public static String normalizeVi(String s) {
        if (s == null) return null;
        String nfd = Normalizer.normalize(s, Normalizer.Form.NFD);

        String noDiacritics = nfd.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        noDiacritics = noDiacritics
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return noDiacritics.replaceAll("\\s+", " ").trim();
    }

    // So sánh không phân biệt hoa thường và bỏ dấu
    public static boolean equalsViIgnoreCaseNoAccent(String a, String b) {
        if (a == null || b == null) return true;
        String na = normalizeVi(a).toLowerCase();
        String nb = normalizeVi(b).toLowerCase();
        return na.equals(nb);
    }


    public static String removeVietnameseAccentWithTime(String input) {
        if (input == null || input.isBlank()) {
            return "file_" + LocalDateTime.now().format(FILE_TIME_FORMAT);
        }

        // Tách tên file & extension
        int dotIndex = input.lastIndexOf('.');
        String name = dotIndex > 0 ? input.substring(0, dotIndex) : input;
        String ext = dotIndex > 0 ? input.substring(dotIndex) : "";

        // Bỏ dấu
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("đ", "d")
                .replace("Đ", "D");

        // Làm an toàn cho filename
        normalized = normalized
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_+", "_");

        String timestamp = LocalDateTime.now().format(FILE_TIME_FORMAT);

        return normalized + "_" + timestamp + ext;
    }
}

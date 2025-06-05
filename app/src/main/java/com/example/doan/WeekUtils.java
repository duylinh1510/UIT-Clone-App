package com.example.doan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekUtils {

    // Class để chứa thông tin ngày
    public static class DayInfo {
        public String dayName;      // "CN", "Hai", "Ba", ...
        public String dayNumber;    // "25", "26", "27", ...
        public String fullDate;     // "Chủ Nhật - 25/05/2025"
        public boolean isToday;     // true nếu là ngày hôm nay

        public DayInfo(String dayName, String dayNumber, String fullDate, boolean isToday) {
            this.dayName = dayName;
            this.dayNumber = dayNumber;
            this.fullDate = fullDate;
            this.isToday = isToday;
        }
    }

    //Trả về danh sách 7 ngày của tuần hiện tại, từ Chủ Nhật → Thứ Bảy, với thông tin chi tiết
    public static List<DayInfo> getCurrentWeekDays() {
        List<DayInfo> result = new ArrayList<>();

        // Lấy ngày hiện tại để so sánh
        Calendar today = Calendar.getInstance();
        int todayYear = today.get(Calendar.YEAR);
        int todayMonth = today.get(Calendar.MONTH);
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();

        // Tìm về Chủ Nhật (Calendar.SUNDAY = 1)
        // lùi về ngày Chủ Nhật đầu tuần hiện tại, (nếu hôm nay là Thứ Ba → lùi 2 ngày, v.v.)
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = currentDayOfWeek - Calendar.SUNDAY;
        calendar.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        // Mảng tên ngày theo thứ tự từ Chủ Nhật
        String[] dayNames = {"CN", "Hai", "Ba", "Tư", "Năm", "Sáu", "Bảy"};
        String[] fullDayNames = {"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
        SimpleDateFormat dayNumberFormat = new SimpleDateFormat("d", new Locale("vi", "VN"));

        //Duyệt 7 ngày liên tục, mỗi lần +1 ngày → xây dựng danh sách DayInfo.
        for (int i = 0; i < 7; i++) {
            String dayName = dayNames[i];
            String dayNumber = dayNumberFormat.format(calendar.getTime());
            String fullDate = fullDayNames[i] + " - " + dateFormat.format(calendar.getTime());

            // Kiểm tra có phải ngày hôm nay không
            boolean isToday = (calendar.get(Calendar.YEAR) == todayYear &&
                    calendar.get(Calendar.MONTH) == todayMonth &&
                    calendar.get(Calendar.DAY_OF_MONTH) == todayDay);

            result.add(new DayInfo(dayName, dayNumber, fullDate, isToday));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }

    // Lấy tên tháng hiện tại
    //Trả về tên tháng hiện tại bằng tiếng Việt (VD: "Tháng 5 2025")
    //và viết hoa chữ cái đầu nhờ capitalizeFirstLetter().
    public static String getCurrentMonthName() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("vi", "VN"));
        return capitalizeFirstLetter(monthFormat.format(calendar.getTime()));
    }

    // Viết hoa chữ cái đầu
    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    // Method cũ để tương thích ngược (deprecated)
    @Deprecated
        public static List<String> getCurrentWeekDaysInVietnamese() {
        List<String> result = new ArrayList<>();
        List<DayInfo> dayInfos = getCurrentWeekDays();
        for (DayInfo dayInfo : dayInfos) {
            result.add(dayInfo.fullDate);
        }
        return result;
    }
}
package com.example.doan;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * BaseActivity chứa các phương thức chung cho tất cả Activity
 * Các Activity khác sẽ kế thừa từ class này để tái sử dụng code
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Thiết lập navigation cho bottom nav
     * Phương thức này sẽ được gọi trong onCreate() của các Activity con
     */
    protected void setupNavigation() {
        // Thời khóa biểu
        LinearLayout navThoiKhoaBieu = findViewById(R.id.navThoiKhoaBieu);
        if (navThoiKhoaBieu != null) {
            navThoiKhoaBieu.setOnClickListener(v -> {
                if (!(this instanceof ScheduleActivity)) {
                    Intent intent = new Intent(this, ScheduleActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Lịch thi
        LinearLayout navLichThi = findViewById(R.id.navLichThi);
        if (navLichThi != null) {
            navLichThi.setOnClickListener(v -> {
                if (!(this instanceof LichThiActivity)) {
                    Intent intent = new Intent(this, LichThiActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Điểm
        LinearLayout navDiem = findViewById(R.id.navDiem);
        if (navDiem != null) {
            navDiem.setOnClickListener(v -> {
                if (!(this instanceof GradeActivity)) {
                    Intent intent = new Intent(this, GradeActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Cá nhân
        LinearLayout navCaNhan = findViewById(R.id.navCaNhan);
        if (navCaNhan != null) {
            navCaNhan.setOnClickListener(v -> {
                if (!(this instanceof ProfileActivity)) {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Thêm các navigation khác nếu có
        // Ví dụ: Học phí, Thông báo...
        
        // Đăng xuất (nếu có)
//        LinearLayout navLogout = findViewById(R.id.navLogout);
//        if (navLogout != null) {
//            navLogout.setOnClickListener(v -> {
//                logout();
//            });
//        }

        // Highlight navigation item hiện tại
        highlightCurrentNavItem();
    }

    /**
     * Highlight navigation item hiện tại
     */
    protected void highlightCurrentNavItem() {
        // Reset tất cả navigation items về trạng thái bình thường
        resetNavItemStyle(R.id.navThoiKhoaBieu);
        resetNavItemStyle(R.id.navLichThi);
        resetNavItemStyle(R.id.navDiem);
        resetNavItemStyle(R.id.navCaNhan);

        // Highlight item hiện tại
        if (this instanceof ScheduleActivity) {
            highlightNavItem(R.id.navThoiKhoaBieu);
        } else if (this instanceof LichThiActivity) {
            highlightNavItem(R.id.navLichThi);
        } else if (this instanceof GradeActivity) {
            highlightNavItem(R.id.navDiem);
        } else if (this instanceof ProfileActivity) {
            highlightNavItem(R.id.navCaNhan);
        }
    }

    /**
     * Highlight một navigation item
     */
    private void highlightNavItem(int navItemId) {
        LinearLayout navItem = findViewById(navItemId);
        if (navItem != null) {
            // Thay đổi background color
            navItem.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            
            // Thay đổi text color của tất cả TextView con (nếu có)
            for (int i = 0; i < navItem.getChildCount(); i++) {
                if (navItem.getChildAt(i) instanceof TextView) {
                    TextView textView = (TextView) navItem.getChildAt(i);
                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                }
            }
        }
    }

    /**
     * Reset style của navigation item về bình thường
     */
    private void resetNavItemStyle(int navItemId) {
        LinearLayout navItem = findViewById(navItemId);
        if (navItem != null) {
            // Reset background
            navItem.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            
            // Reset text color
            for (int i = 0; i < navItem.getChildCount(); i++) {
                if (navItem.getChildAt(i) instanceof TextView) {
                    TextView textView = (TextView) navItem.getChildAt(i);
                    textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                }
            }
        }
    }

    /**
     * Phương thức đăng xuất chung
     */
    protected void logout() {
        SessionManager sessionManager = new SessionManager(this);
        sessionManager.logout();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Hiển thị active state cho navigation item hiện tại
     * Phương thức này có thể được override ở các Activity con
     */
    protected void setActiveNavigation() {
        // Override ở Activity con để highlight navigation item hiện tại
    }
} 
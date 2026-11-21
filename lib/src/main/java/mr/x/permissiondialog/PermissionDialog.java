package mr.x.permissiondialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PermissionDialog {

    private static AlertDialog currentDialog;
    private static Activity currentActivity;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static Runnable refreshRunnable;
    private static boolean isAutoRefresh = false;
    private static LinearLayout currentPermissionsLayout;
    private static Cqd callback;

    public interface Cqd {
        void djk(String result, String pd);
    }

    public static void Async(Cqd cqd) {
        callback = cqd;
    }

    public static void setActivity(Activity activity) {
        currentActivity = activity;
    }

    public static void qdjc(Activity activity) {
        currentActivity = activity;
        if (!hasAllFileAccessPermission()) {
            showPermissionDialog(activity);
        } else {
            if (callback != null) {
                callback.djk("true", "true");
            }
        }
    }

    public static void jckq(Activity activity) {
        currentActivity = activity;
        if (currentDialog != null && currentDialog.isShowing()) {
            refreshDialogContent();
        } else {
            qdjc(activity);
        }
    }

    private static void showPermissionDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LinearLayout mainLayout = createMainLayout(activity);

        TextView titleView = createTitleView(activity, "权限申请");
        mainLayout.addView(titleView);

        TextView subtitleView = createSubtitleView(activity, "应用需要以下权限才能正常使用");
        mainLayout.addView(subtitleView);

        currentPermissionsLayout = createPermissionsLayout(activity);
        mainLayout.addView(currentPermissionsLayout);

        refreshPermissionsLayout(currentPermissionsLayout);

        LinearLayout buttonLayout = createButtonLayout(activity);
        mainLayout.addView(buttonLayout);

        builder.setView(mainLayout);

        currentDialog = builder.create();
        currentDialog.setCancelable(false);
        currentDialog.show();

        // 关键：show 之后约束窗口宽度，避免内容撑出屏幕
        Window window = currentDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(createRoundRectDrawable(activity, 28, Color.WHITE));
            int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
            int width = (int) (screenWidth * 0.92f);
            int maxWidth = dpToPx(activity, 400);
            if (width > maxWidth) {
                width = maxWidth;
            }
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        startAutoRefresh();
    }

    private static LinearLayout createButtonLayout(Context context) {
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.END);
        buttonLayout.setPadding(0, dpToPx(context, 16), 0, 0);

        Button positiveButton = new Button(context);
        positiveButton.setText("我知道了");

        setupPrimaryButtonStyle(positiveButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAutoRefresh();

                boolean granted = hasAllFileAccessPermission();

                if (callback != null) {
                    callback.djk("true", granted ? "true" : "false");
                }

                if (currentDialog != null && currentDialog.isShowing()) {
                    currentDialog.dismiss();
                }
                currentDialog = null;
                currentPermissionsLayout = null;
            }
        });

        buttonLayout.addView(positiveButton);
        return buttonLayout;
    }

    private static void startAutoRefresh() {
        stopAutoRefresh();
        isAutoRefresh = true;
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (isAutoRefresh && currentDialog != null && currentDialog.isShowing() && currentActivity != null) {
                    refreshDialogContent();
                    if (isAutoRefresh) {
                        handler.postDelayed(this, 1000);
                    }
                }
            }
        };
        handler.postDelayed(refreshRunnable, 1000);
    }

    private static void stopAutoRefresh() {
        isAutoRefresh = false;
        if (refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
            refreshRunnable = null;
        }
    }

    private static void refreshDialogContent() {
        if (currentDialog == null || currentActivity == null || currentPermissionsLayout == null) return;

        refreshPermissionsLayout(currentPermissionsLayout);

        if (hasAllFileAccessPermission()) {
            stopAutoRefresh();
        }
    }

    private static void refreshPermissionsLayout(LinearLayout permissionsLayout) {
        if (currentActivity == null || permissionsLayout == null) return;

        permissionsLayout.removeAllViews();

        boolean hasFilePermission = hasAllFileAccessPermission();
        addPermissionItem(permissionsLayout,
                "所有文件访问权限",
                "允许应用访问设备上的所有文件",
                hasFilePermission ? "已开启" : "未开启",
                hasFilePermission,
                hasFilePermission ? null : new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAllFileAccessPermission(currentActivity);
                    }
                });
    }

    private static LinearLayout createMainLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(context, 20), dpToPx(context, 20), dpToPx(context, 20), dpToPx(context, 12));
        layout.setBackground(createRoundRectDrawable(context, 16, Color.WHITE));
        return layout;
    }

    private static TextView createTitleView(Context context, String title) {
        TextView titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setTextColor(Color.parseColor("#1A1A1A"));
        titleView.setGravity(Gravity.START);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dpToPx(context, 8);
        titleView.setLayoutParams(params);
        return titleView;
    }

    private static TextView createSubtitleView(Context context, String subtitle) {
        TextView subtitleView = new TextView(context);
        subtitleView.setText(subtitle);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        subtitleView.setTextColor(Color.parseColor("#666666"));
        subtitleView.setGravity(Gravity.START);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dpToPx(context, 16);
        subtitleView.setLayoutParams(params);
        return subtitleView;
    }

    private static LinearLayout createPermissionsLayout(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackground(createRoundRectDrawable(context, 12, Color.parseColor("#F8F9FA")));
        layout.setPadding(dpToPx(context, 12), dpToPx(context, 12), dpToPx(context, 12), dpToPx(context, 12));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dpToPx(context, 20);
        layout.setLayoutParams(params);
        return layout;
    }

    private static void addPermissionItem(LinearLayout parent,
                                          String permissionName, String description,
                                          String status, boolean isEnabled,
                                          View.OnClickListener clickListener) {
        Context context = parent.getContext();

        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (parent.getChildCount() > 0) {
            itemParams.topMargin = dpToPx(context, 12);
        }
        itemLayout.setLayoutParams(itemParams);

        // 文本区：权重 1，会自动压缩，避免把右侧元素挤出屏幕
        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        textLayout.setLayoutParams(textParams);

        TextView nameText = new TextView(context);
        nameText.setText(permissionName);
        nameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        nameText.setTextColor(Color.parseColor("#1A1A1A"));
        nameText.setSingleLine(true);
        nameText.setEllipsize(android.text.TextUtils.TruncateAt.END);

        TextView descText = new TextView(context);
        descText.setText(description);
        descText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        descText.setTextColor(Color.parseColor("#888888"));
        descText.setPadding(0, dpToPx(context, 2), 0, 0);
        descText.setMaxLines(2);
        descText.setEllipsize(android.text.TextUtils.TruncateAt.END);

        textLayout.addView(nameText);
        textLayout.addView(descText);

        // 状态文本：固定 wrap_content，单行不换行
        TextView statusText = new TextView(context);
        statusText.setText(status);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        statusText.setTypeface(null, android.graphics.Typeface.BOLD);
        statusText.setTextColor(isEnabled ? Color.parseColor("#4CAF50") : Color.parseColor("#FF6B6B"));
        statusText.setSingleLine(true);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        statusParams.leftMargin = dpToPx(context, 4);
        statusText.setLayoutParams(statusParams);

        // 操作按钮：清零默认最小尺寸，避免 Material 主题的默认 minWidth/inset 撑宽布局
        Button actionButton = new Button(context);
        actionButton.setAllCaps(false);
        actionButton.setIncludeFontPadding(false);
        actionButton.setMinWidth(0);
        actionButton.setMinimumWidth(0);
        actionButton.setMinHeight(0);
        actionButton.setMinimumHeight(0);

        if (clickListener != null) {
            actionButton.setText("去开启");
            actionButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            actionButton.setOnClickListener(clickListener);
            setupSecondaryButtonStyle(actionButton);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnParams.leftMargin = dpToPx(context, 6);
            actionButton.setLayoutParams(btnParams);
        } else {
            actionButton.setVisibility(View.GONE);
        }

        itemLayout.addView(textLayout);
        itemLayout.addView(statusText);
        itemLayout.addView(actionButton);

        parent.addView(itemLayout);
    }

    private static GradientDrawable createRoundRectDrawable(Context context, int cornerRadius, int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(dpToPx(context, cornerRadius));
        shape.setColor(color);
        shape.setStroke(dpToPx(context, 1), Color.parseColor("#20000000"));
        return shape;
    }

    private static void setupPrimaryButtonStyle(Button button) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(dpToPx(button.getContext(), 22));
        shape.setColor(Color.parseColor("#6750A4"));

        button.setBackground(shape);
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        button.setAllCaps(false);
        button.setIncludeFontPadding(false);
        button.setPadding(
                dpToPx(button.getContext(), 24),
                dpToPx(button.getContext(), 10),
                dpToPx(button.getContext(), 24),
                dpToPx(button.getContext(), 10)
        );
        button.setMinWidth(0);
        button.setMinimumWidth(0);
    }

    private static void setupSecondaryButtonStyle(Button button) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(dpToPx(button.getContext(), 14));
        shape.setColor(Color.TRANSPARENT);
        shape.setStroke(dpToPx(button.getContext(), 1), Color.parseColor("#6750A4"));

        button.setBackground(shape);
        button.setTextColor(Color.parseColor("#6750A4"));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        button.setAllCaps(false);
        button.setIncludeFontPadding(false);
        button.setPadding(
                dpToPx(button.getContext(), 10),
                dpToPx(button.getContext(), 4),
                dpToPx(button.getContext(), 10),
                dpToPx(button.getContext(), 4)
        );
    }

    private static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private static boolean hasAllFileAccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                return android.os.Environment.isExternalStorageManager();
            } catch (Exception e) {
                return true;
            }
        }
        return true;
    }

    private static void openAllFileAccessPermission(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                } else {
                    openAppSettings(activity);
                }
            } else {
                openAppSettings(activity);
            }
        } catch (Exception e) {
            openAppSettings(activity);
        }
    }

    private static void openAppSettings(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
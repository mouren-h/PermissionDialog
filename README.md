<div align="center">

# 🛡️ PermissionDialog

**轻量、零依赖的 Android 权限申请弹窗组件**

自动检测「所有文件访问权限」（MANAGE_EXTERNAL_STORAGE），Material Design 3 风格弹窗，实时刷新权限状态，一行代码完成权限引导。

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/minSdk-21-blue)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java-orange?logo=openjdk&logoColor=white)](https://www.java.com)
[![License](https://img.shields.io/badge/License-MIT-green)](./LICENSE)
[![GitHub Stars](https://img.shields.io/github/stars/mouren-h/PermissionDialog?style=social)](https://github.com/mouren-h/PermissionDialog)

</div>

---

## 📖 目录

- [✨ 特性](#-特性)
- [📱 效果预览](#-效果预览)
- [📦 集成方式](#-集成方式)
- [🚀 快速开始](#-快速开始)
- [📚 API 说明](#-api-说明)
- [⚠️ 注意事项](#️-注意事项)
- [🧩 兼容性](#-兼容性)
- [📄 License](#-license)

---

## ✨ 特性

- 🎯 **单一职责** —— 专为「所有文件访问权限」设计，轻量无冗余
- 🪄 **一行调用** —— `PermissionDialog.qdjc(this)` 即可完成检测与弹窗
- 🔄 **实时刷新** —— 弹窗展示期间每秒自动检测，开启后状态即时更新
- 🎨 **MD3 风格** —— 圆角卡片、Material You 主色（`#6750A4`），视觉统一
- 📐 **自适应布局** —— 弹窗宽度自动限制为屏宽 92%（最大 400dp），窄屏/大字号不溢出
- 🔁 **生命周期友好** —— 配合 `jckq()` 在 `onResume` 中刷新，从设置页返回无缝衔接
- 🧩 **零第三方依赖** —— 仅使用 Android 官方 API，无额外库

---

## 📱 效果预览

<div align="center">

| 权限未开启 | 权限已开启 |
| :---: | :---: |
| ![未开启](docs/preview-dialog.gif) | ![已开启](docs/preview-granted.gif) |

> 💡 建议在仓库中新建 `docs` 文件夹，放入你实际运行的截图或 GIF，并替换上方链接。

</div>

---

## 📦 集成方式

### 方式一：直接复制文件

将 `PermissionDialog.java` 复制到你的 Android 项目中，建议包名为 `com.yxbb`（或自行修改 `package` 声明与调用处）。

### 方式二：Git Clone

```bash
git clone https://github.com/mouren-h/PermissionDialog.git
```

> 项目基于 Java 编写，不依赖 Kotlin 或 AndroidX 之外的任何第三方库。

---

## 🚀 快速开始

### 1️⃣ 设置回调（可选）

如果你需要在权限检测完成后拿到结果：

```java
PermissionDialog.Async(new PermissionDialog.Cqd() {
    @Override
    public void djk(String result, String pd) {
        // result 固定为 "true"
        // pd 为 "true" 表示权限已开启，"false" 表示未开启
        if ("true".equals(pd)) {
            // 权限已就绪，可执行文件操作
        } else {
            // 权限未开启，引导用户再次开启
        }
    }
});
```

### 2️⃣ 启动权限检测

在你的 Activity 中调用：

```java
PermissionDialog.qdjc(this);
```

- 若权限已开启 → 直接触发回调，**不弹窗**
- 若权限未开启 → 弹出 MD3 风格引导弹窗

### 3️⃣ 返回时刷新（强烈推荐）

从系统设置页返回应用时，同步刷新弹窗状态：

```java
@Override
protected void onResume() {
    super.onResume();
    PermissionDialog.jckq(this);
}
```

> `jckq()` 会判断弹窗是否正在显示：显示中则刷新内容，未显示则重新检测。

---

## 📚 API 说明

### 公开方法

| 方法 | 参数 | 说明 |
| :--- | :--- | :--- |
| `Async(Cqd)` | `Cqd` 回调接口 | 设置权限检测结果回调 |
| `qdjc(Activity)` | 宿主 Activity | **主入口**，检测权限并弹窗 |
| `jckq(Activity)` | 宿主 Activity | 刷新弹窗 / 重新检测（建议在 `onResume` 调用） |
| `setActivity(Activity)` | 宿主 Activity | 预先设置 Activity 引用（用于异步场景） |

### 回调接口 `Cqd`

```java
public interface Cqd {
    void djk(String result, String pd);
}
```

| 参数 | 类型 | 说明 |
| :--- | :--- | :--- |
| `result` | `String` | 固定为 `"true"`，表示检测流程已完成 |
| `pd` | `String` | `"true"` 权限已开启；`"false"` 权限未开启 |

---

## ⚠️ 注意事项

1. **仅处理 `MANAGE_EXTERNAL_STORAGE`**
   本项目专用于「所有文件访问权限」，不涉及运行时权限（如相机、定位）。如需通用权限申请，请使用 `ActivityResultContracts` 或 EasyPermissions。

2. **Android 11 (API 30) 以下**
   低版本无「所有文件访问权限」概念，`hasAllFileAccessPermission()` 直接返回 `true`，`qdjc()` 不会弹窗。

3. **Google Play 政策**
   上架 Google Play 时，使用 `MANAGE_EXTERNAL_STORAGE` 需在 Play Console 提交**权限声明**并说明用途，否则可能被拒。

4. **单例约束**
   `PermissionDialog` 内部使用静态状态，**同一时刻仅支持一个弹窗实例**。

---

## 🧩 兼容性

| 项目 | 支持范围 |
| :--- | :--- |
| 最低 SDK | API 21（Android 5.0） |
| 目标 SDK | API 34+ |
| 语言 | Java |
| UI 风格 | Material Design 3 |
| 依赖 | 仅 Android 官方 SDK |

---

## 🤝 贡献

欢迎提交 [Issue](https://github.com/mouren-h/PermissionDialog/issues) 与 [Pull Request](https://github.com/mouren-h/PermissionDialog/pulls)！

1. Fork 本仓库
2. 新建分支 `feature/your-feature`
3. 提交更改并 Push
4. 发起 Pull Request

---

## 📄 License

```
MIT License

Copyright (c) 2026 mouren-h

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐ Star 支持一下！**

Made with ❤️ by [mouren-h](https://github.com/mouren-h)

</div>
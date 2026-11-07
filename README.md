# StreamX Live — Native Android Live Streaming Application

**StreamX Live** is a next-generation Android Live Streaming application built with **Kotlin**, **Jetpack Compose**, and **Material 3**. It features a modern **Liquid Glass** aesthetic, real-time live video broadcasting with **CameraX**, interactive live chat, dynamic heart burst reactions, virtual gifting with celebratory animations, and a secure **Indian Rupee (₹) / Razorpay** monetization system.

---

## 📲 Download & Install APK

You can directly download and install the compiled APK on your Android device:

👉 **[Download StreamX-Live-v1.0.apk (Direct Download)](https://github.com/Vipulbaldha23/Streamx/raw/main/release/StreamX-Live-v1.0.apk)**

- **File Name**: `StreamX-Live-v1.0.apk`
- **Size**: ~26 MB
- **Target Android Version**: Android 7.0 (API 24) to Android 15 / 16 (API 36)
- **Architecture**: Universal APK (arm64-v8a, armeabi-v7a, x86_64)

---

## 🌟 Key Features

### 1. Futuristic Liquid Glass UI
- Translucent frosted glass cards with gradient glowing borders and soft neon glows.
- Futuristic dark theme default with cyan (`#00F2FE`), magenta/violet (`#8B5CF6`), streaming live red (`#FF0055`), and gold (`#FFB800`).
- Seamless dynamic light/dark theme toggle.
- Edge-to-edge support with custom floating bottom navigation bar.

### 2. Live Broadcasting Engine
- **CameraX** live video feed with front and back camera switching (`ProcessCameraProvider`).
- Microphone mute/unmute control.
- Pre-broadcast setup: Stream title, category selection (*Gaming, Tech, Music, IRL, Fitness, Esports*), and resolution/quality configuration.
- Real-time telemetry HUD: Live duration timer, viewer count, bitrate (kbps), 60 FPS monitor, and connection latency (ping).
- Live comments overlay directly over the camera feed.
- Post-stream summary analytics: Peak viewers, broadcast duration, and diamonds/coins earned.

### 3. Interactive Live Player
- Full-screen live video player with custom controls.
- Real-time interactive chat with streamer, moderator, and VIP badges.
- Double-tap and button-triggered floating heart reaction burst (`FloatingReactionsContainer`).
- Stream quality switcher (*Auto 1080p, 720p HD, 480p Data Saver*).
- Stream reporting dialog for content moderation.
- Picture-in-Picture (PiP) mode support.

### 4. Legal Monetization & Indian Payment System (INR ₹)
- **Currency**: Indian Rupee (**₹**).
- Official Indian payment gateway architecture (Razorpay UPI, Cards, Net Banking, and Google Play In-App Billing).
- Configurable coin recharge packages with 18% GST calculation and tax receipts:
  - 100 Coins = ₹100
  - 500 Coins (+50 Free) = ₹480
  - 1,200 Coins (+150 Free) = ₹1,000
  - 3,000 Coins (+500 Free) = ₹2,400
- Virtual gifts catalog (*Rose 🌹, Heart Blast 💖, Cyber Bolt ⚡, Stream Crown 👑, Falcon Rocket 🚀, Diamond Palace 💎, Super Phoenix 🔥*).
- Full-screen celebratory gift overlay animations.
- Creator revenue split (70% Creator / 30% Platform) with verified KYC (PAN + Bank Settlement details) and withdrawal requests.

### 5. Creator & Admin Dashboards
- **Creator Studio**: Total streams, peak concurrent viewers, broadcast duration, pending payout balance, and withdrawal requests.
- **Admin Dashboard**: Live stream monitor, emergency **Terminate Stream** functionality, user suspension/ban management, user reports queue, and platform fee / coin rate configuration.

### 6. Search, Discovery & Following
- Multi-filter discovery across categories and languages (English, Hindi).
- Channels You Follow screen with live status badges.
- In-app notification center for live stream alerts, gifts, and payment receipts.

---

## 🛠 Tech Stack

- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose & Material Design 3
- **Camera**: CameraX 1.5.0 (`camera2`, `lifecycle`, `view`, `core`)
- **Image Loading**: Coil Compose 2.7.0
- **Navigation**: Navigation Compose 2.8.9
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Data Persistence**: Android Room 2.7.0 & DataStore
- **Build System**: Android Gradle Plugin 9.1.1, Java 11, Target SDK 36

---

## 🚀 Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/Vipulbaldha23/Streamx.git
   ```
2. Open the project in Android Studio (Ladybug or newer).
3. Let Gradle sync and build the project.
4. Run on your device or emulator.

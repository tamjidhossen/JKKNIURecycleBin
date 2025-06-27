# JKKNIU Recycle Bin

A comprehensive buy-sell marketplace Android application for students of Jatiya Kabi Kazi Nazrul Islam University (JKKNIU). This mobile app enables students to buy and sell second-hand items, textbooks, electronics, and other goods within the university community, promoting sustainability through reuse.

## 🌟 Features

### User Management

- **Student Registration & Authentication**: Secure login system with email verification
- **Profile Management**: Students can update their profiles and view their transaction history
- **Student Verification**: Email-based verification system for JKKNIU students
- **Like-based Rating System**: Facebook-style like system for users and items

### Marketplace Features

- **Post Items for Sale**: Easy listing creation with photos, descriptions, and pricing
- **Browse & Search**: Advanced search and filtering options by category
- **Favorites**: Save items for later viewing
- **Category Management**: Organized listings by books, electronics, furniture, etc.

### Transaction Management

- **Meet-up Coordination**: Schedule and coordinate item exchanges on campus

### Admin Features

- **Student Account Management**: Administrators can manage student accounts
- **Listing Oversight**: Monitor and moderate item listings for policy compliance

### Mobile-First Design

- **Native Android Experience**: Built with Android Studio for optimal performance
- **Material Design**: Google's Material Design principles for intuitive UI

## 🛠️ Technology Stack

### Android Development

- **Java**: Primary programming language
- **Android Studio**: Official IDE for Android development
- **Material Design Components**: Google's design system for beautiful UI
- **View Binding**: Modern view binding for cleaner code
- **Glide**: Image loading and caching library

### Firebase Backend

- **Firebase Realtime Database**: Real-time data synchronization
- **Firebase Authentication**: Secure email-based authentication with verification
- **Firebase Storage**: Cloud storage for images and files
- **Firebase Crashlytics**: Crash reporting and analytics

### Key Features

- **Email Verification**: Firebase email verification system
- **Real-time Updates**: Instant data synchronization across devices
- **Image Handling**: Camera integration and photo uploads
- **Material Design**: Modern Android UI/UX patterns

## 🔥 Firebase Database Structure

The app uses Firebase Realtime Database with the following structure:

```
jkkniu-marketplace
├── Ads/
│   └── {adId}/
│       ├── id: String
│       ├── title: String
│       ├── description: String
│       ├── price: String
│       ├── category: String
│       ├── condition: String
│       ├── brand: String
│       ├── address: String
│       ├── latitude: Double
│       ├── longitude: Double
│       ├── timestamp: Long
│       ├── adOwnerName: String
│       ├── vid: String (vendor/seller ID)
│       ├── status: String
│       ├── favoriteCount: Integer
│       └── images/
├── Users/
│   └── {userId}/
│       ├── name: String
│       ├── email: String
│       ├── timestamp: Long
│       └── profileImageUrl: String
└── Favorites/
    └── {userId}/
        └── {adId}: Boolean
```

## 📦 Dependencies

```gradle
// Firebase
implementation 'com.google.firebase:firebase-analytics:21.5.0'
implementation 'com.google.firebase:firebase-auth:22.3.0'
implementation 'com.google.firebase:firebase-database:20.3.0'
implementation 'com.google.firebase:firebase-storage:20.3.0'
implementation 'com.google.firebase:firebase-crashlytics:18.6.0'

// Android UI
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.8.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// Image loading
implementation 'com.github.bumptech.glide:glide:4.16.0'
```

## 📱 Screenshots

### Login Screen

![Login](docs/images/login.jpg)
_Secure login interface with email authentication_

### Continue with Email

![Continue with Email](docs/images/continue%20with%20mail%20page.jpg)
_Email verification process for students_

### Home Screen

![Home Screen](docs/images/home.jpg)
_Clean and intuitive home screen with featured items and category navigation_

### Ads Listings

![Ads](docs/images/ads.jpg)
_Browse through various items posted by students_

### More Ads

![More Ads](docs/images/more%20ads.jpg)
_Extended view of available items with like counts_

### Upload Ad

![Upload Ad](docs/images/upload%20ad.jpg)
_Easy-to-use interface for creating new item listings_

### Upload Ad Details

![Upload Ad 2](docs/images/upload%20ad%202.jpg)
_Additional details form for posting items_

### Ad Details

![Ad Details](docs/images/ad%20details.jpg)
_Detailed view of items with photos, descriptions, and seller information_

### My Ads

![My Ads](docs/images/my%20ads.jpg)
_View and manage your posted items_

### Profile Section

![Profile](docs/images/profile%20section.jpg)
_Student profile with personal information and settings_

### Profile Edit

![Profile Edit](docs/images/profile%20edit%20section.jpg)
_Edit profile information and preferences_

### Favorites

![Favorites](docs/images/favourite.jpg)
_View liked items with Facebook-style like system_

### Ad Report

![Ad Report](docs/images/ad%20report.jpg)
_Report inappropriate content or ads_

### Dark Mode

![Dark Mode 1](docs/images/darkmorde1.jpg) ![Dark Mode 2](docs/images/darkmorde2.jpg)
_Beautiful dark theme support throughout the app_

## 🚀 Installation

### Prerequisites

- Android Studio Arctic Fox or higher
- Android SDK (API level 21 or higher)
- Java 8 or higher
- Firebase project setup

### Android App Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/jkkniu-recycle-bin.git
   cd jkkniu-recycle-bin
   ```

2. **Open in Android Studio**

   - Open Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned repository folder

3. **Firebase Configuration**

   - Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Firebase Authentication (Email/Password)
   - Enable Firebase Realtime Database
   - Enable Firebase Storage
   - Set up Firebase Crashlytics

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio

## 🗂️ Project Structure

```
jkkniu-recycle-bin/
├── app/
│   ├── src/main/java/com/example/recyclebin/
│   │   ├── activities/           # All app activities
│   │   │   ├── MainActivity.java
│   │   │   ├── LoginEmailActivity.java
│   │   │   ├── RegisterEmailActivity.java
│   │   │   ├── AdCreateActivity.java
│   │   │   ├── AdDetailsActivity.java
│   │   │   └── ProfileEditActivity.java
│   │   ├── adapters/            # RecyclerView adapters
│   │   ├── fragments/           # App fragments
│   │   │   ├── HomeFragment.java
│   │   │   ├── ChatsFragment.java
│   │   │   ├── MyAdsFragment.java
│   │   │   └── AccountFragment.java
│   │   ├── models/              # Data models
│   │   │   ├── ModelAd.java
│   │   │   ├── ModelReport.java
│   │   │   └── ModelImageSlider.java
│   │   ├── FilterAd.java        # Ad filtering logic
│   │   ├── Utils.java           # Utility functions
│   │   └── RvListenerCategory.java
│   ├── src/main/res/            # Android resources
│   │   ├── layout/              # XML layouts
│   │   ├── drawable/            # Icons and images
│   │   ├── values/              # Colors, strings, styles
│   │   └── menu/                # Menu resources
│   ├── google-services.json     # Firebase configuration
│   └── build.gradle             # App-level build config
├── docs/
│   └── images/                  # Documentation screenshots
├── gradle/                      # Gradle wrapper files
├── build.gradle                 # Project-level build config
└── README.md                    # This file
```

## 🤝 Contributing

We welcome contributions to improve the JKKNIU Recycle Bin project! Create a PR to start.

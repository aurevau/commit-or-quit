
# Commit Or Quit - Android
This project is an Android implementation of a goal tracking app, built in Kotlin using XML-based layouts.
Where users create personal or group-based goals, invite friends, track progress and stay accountable through
updates, media sharing and notifications. 

The app focuses on commitment, social motivation, and structured goal-tracking in a clean Material Design UI. 

Built as a part of an Android development project as a side project. 


## Current Status
Under development

## App Concept 
Users can: 
- Create goals with start & end dates
- Choose update frequency for notifications to be held accountable
- Set privacy levels (Public/Members only)
- Invite friends to goals
- Post updates with images or videos
- Track progress over time
- Recieve invitations & notifications
- Manage profile & onboarding flow

## Features 
- **Firebase Authentication** (Email & Google-Sign-In)
- Onboarding flow for profile creation
- Goal creation in BottomSheet dialog
- Member selection with chips + recycler
- Media uploads to **Firebase Storage**
- Privacy system for goals & updates
- Initation system
- MVVM architecture
- Live updates using **StateFlow**
- Navigation Component + BottomNavigation
- Profile editing
- Animated bottom sheet dialogs
- Notification panel
- Settings screen & logout

## Tech Stack
- Kotlin
- Android SDK
- XML-based layouts
- Material Design Components
- Firebase Auth
- Firebase Storage
- Firestore
- MVVM Architecture
- ViewModel
- StateFlow / LiveData
- Fragments
- Navigation Component
- ViewBinding
- Glide
- Coroutines

## Architecture
The project follows MVVM: 
UI (Fragments / Activities)

↓

ViewModels
(AuthViewModel, GoalViewModel, UserViewModel)

↓

Repositories
(UserRepository, GoalRepository, MediaRepository)

↓

Firebase Services

## Key Components
### Models
- User
- Goal
- GoalUpdate
- Invitation
- Notification
- MediaItem
- Privacy

### ViewModels
- AuthViewModel: authentication & onboarding logic
- GoalViewModel: goal creation & state handling
- UserViewModel: users & profile data

### UI
- LoginActivity
- OnboardingActivity
- MainActivity
- CreateGoalFragment
- SelectUsersFragment
- NotificationFragment
- ProfileFragment
- SettingsFragment

## How to Run
1. Clone the repository
2. Open Android Studio
3. Select Open and choose the project folder
4. Wait for Gradle sync
5. Add firebase config (google-services.json)
6. Run the app on emulator or device



## Future Features
- Feed timeline for public goals and updates
- Goal detail screen with updates
- Comments & reactions
- Push notifications
- Friend system
- Goal streak tracking
- Analytics dashboard
- Dark Mode
- Theme picker
- Offline caching
- Better separation of domain logic
- UI polish & Animations

Created by **Aurelie Vaudan**

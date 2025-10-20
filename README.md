# 🌿 GreenChain – Gamified Recycling App

> **GreenChain** is a Kotlin-based Android app that makes recycling fun, social, and AI-powered.  
> Developed by a team of Informatics students as part of a university project.

---

## 🚀 Overview
GreenChain encourages users to recycle by turning eco-friendly actions into a game.  
Users can:
- 📸 Scan bottles with the **SGR logo** using AI (RoboFlow).
- 🏅 Earn points and climb the **leaderboard**.
- 🤝 Connect with friends, share eco-posts, and complete daily quests.
- 🗺️ Find nearby recycling spots on an interactive map.

---

## 🎨 Mockups


![mock-up_GreenChain](https://github.com/user-attachments/assets/10e3cad5-4444-45f7-823e-c5c711e48d2b)

---

## ⚙️ Core Features
| Feature | Description |
|----------|--------------|
| **AI Bottle Recognition** | Detect SGR logos via RoboFlow API and CameraX |
| **Leaderboard & Friends** | Track rankings globally or among friends |
| **Social Feed** | Post text, photos, and videos |
| **Daily Quests & Quotes** | Get eco challenges and motivational messages |
| **Recycling Map** | View and navigate to recycling points via Google Maps |
| **Notifications** | Daily reminders and sync tasks with WorkManager |

---

## 🧩 Architecture
**Pattern:** Clean MVVM  
- **Model:** Data (Room, Firebase, APIs)  
- **ViewModel:** Business logic & state  
- **View:** Jetpack Compose UI  

**Main Technologies:**
Kotlin · Jetpack Compose · CameraX · Firebase · RoboFlow · Retrofit · Hilt · Coroutines · WorkManager · Google Maps SDK

---

## 🧱 UML Diagrams

<p align="center">
<img width="1290" height="1705" alt="UseCaseD_GreenChain_UserFacing" src="https://github.com/user-attachments/assets/49fbee45-2dc7-49a6-9321-561a0ff5cb16" />
</p>

<p align="center">
<img width="1071" height="552" alt="UseCaseD_SysContext_GreenChain" src="https://github.com/user-attachments/assets/6be4bcdf-2671-45d0-a5f0-4446d6780b26" />
</p> 

<p align="center">
<img width="2052" height="566" alt="UseCaseD_GreenChain_ScanBottleFlow" src="https://github.com/user-attachments/assets/abf49084-b8f8-4615-a65a-8636337431be" />
</p>

<p align="center">
<img width="1897" height="752" alt="1st_compD_GreenChain" src="https://github.com/user-attachments/assets/2f34ac8b-73c0-4a06-9b76-51d15f4324fb" />
</p>

<p align="center">
<img width="899" height="827" alt="2nd_compD_GreenChain" src="https://github.com/user-attachments/assets/bdc88bd6-5cad-4f85-8a18-f781e224dd63" />
</p>


---

## 🔒 Security
- Firebase Authentication for login  
- Firestore security rules  
- Secure API keys and local permissions handling  

---

## 🧑‍💻 Team
Developed by a 3-person Informatics team passionate about sustainability and mobile innovation:
- Dumitrescu Malina
- Janine Vartolomei
- Bogdan Rista

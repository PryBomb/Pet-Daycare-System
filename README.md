<div align="center">

# 🐾 Pet Daycare Manager 🐾

### *Register, edit and cuddle your daycare pups* 🐶🐱

![Java](https://img.shields.io/badge/Java-8-ff7eb6?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-59a5f0?style=for-the-badge)
![MariaDB](https://img.shields.io/badge/Database-MariaDB-b39dfa?style=for-the-badge&logo=mariadb&logoColor=white)
![XAMPP](https://img.shields.io/badge/Server-XAMPP-fb7a24?style=for-the-badge&logo=xampp&logoColor=white)

☁️ A soft, pastel desktop app that helps daycare staff look after their furry guests ☁️

</div>

---

## 🌸 About the Project

**Pet Daycare Manager** is a desktop application built with **Java Swing** and a **MariaDB** database. It gives daycare staff one cozy place to keep track of every pet, their owners, and their daily attendance, all wrapped in a cute sky-blue and lavender interface with a friendly puppy mascot. 🐕☁️

---

## ✨ Features

### 🐶 Pets
- ➕ **Add pet** records with all the important details
- ✏️ **Save changes** to update an existing pet
- 🧹 **Clear form** to start fresh
- 👀 **View details** of any selected pet
- 🗑️ **Delete selected** pets from the list
- 🔍 **Live search** by name, breed, owner, or address

### 📝 Details stored for each pet
| Field | Description |
|-------|-------------|
| 🆔 ID | Unique number given to each pet |
| 🐾 Name | The pet's name (e.g. Cloud) |
| 🐕 Type | Dog or cat |
| 🦴 Breed | e.g. Shiwawa |
| 🎂 Age | Years and months |
| 👤 Owner | Owner's full name |
| 📞 Phone | Owner's contact number |
| 🏠 Address | Street, barangay, city |

### 📅 Attendance
Keep track of which pets are checked in at the daycare.

### 🐱 Species
Manage the list of species available when registering pets.

---

## 🛠️ Tech Stack

| Tool | Purpose |
|------|---------|
| ☕ **Java (JDK 8)** | Main programming language |
| 🎨 **Java Swing** | Graphical user interface |
| 🗄️ **MariaDB** | Database for pets, attendance, and species |
| 🔌 **MariaDB Java Client 3.5.10** | Connects Java to the database (included in `lib/`) |
| 🧰 **XAMPP** | Runs the local MariaDB/MySQL server |
| 💻 **VS Code** | Development environment |

---

## 📋 Requirements

Before you start, make sure you have:

- ☕ JDK 8 or newer
- 🧰 [XAMPP](https://www.apachefriends.org/) installed
- 💻 VS Code with the **Extension Pack for Java** (or any Java IDE)
- 🔧 [Git](https://git-scm.com/)

---

## 🚀 Getting Started

### 1️⃣ Clone the repository
```bash
git clone https://github.com/PryBomb/Pet-Daycare-System.git
cd Pet-Daycare-System
```

### 2️⃣ Start the database
1. Open the **XAMPP Control Panel**
2. Click **Start** next to **MySQL**
3. Open **phpMyAdmin** at `http://localhost/phpmyadmin`
4. Create the database and tables the app uses

### 3️⃣ Add the database driver
Make sure `lib/mariadb-java-client-3.5.10.jar` is listed under **Referenced Libraries** in VS Code (it's already included in the project).

### 4️⃣ Run the app
Open `src/Pet.java` and click **Run** ▶️ above the `main` method.

---

## 📁 Project Structure

```
Pet-Daycare-System/
├── 📂 .vscode/
│   └── settings.json
├── 📂 lib/
│   └── mariadb-java-client-3.5.10.jar
├── 📂 src/
│   ├── Main.java
│   └── Pet.java        # Main window and app entry point
├── .gitignore
└── README.md
```

---

## 🎨 Design

- 🩵 Sky-blue to lavender gradient background
- ☁️ Fluffy clouds floating around
- 🐕 A friendly puppy mascot
- 🍬 Glossy, candy-style buttons in pink, blue, purple, and red
---

## 👩‍💻 Author

Made with 💖 by **[PryBomb](https://github.com/PryBomb)**

<div align="center">

🐾 *Every pet deserves a happy day at daycare* 🐾

</div>

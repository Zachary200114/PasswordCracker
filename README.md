PasswordCracker
PasswordCracker is a standalone Java Swing application that simulates brute-force password cracking and secure password generation. It was developed as a GUI-based educational and experimental tool to demonstrate the basics of cryptographic hashing, brute-force techniques, and UI/UX design using Java.

Overview
The application supports two primary modes:

Crack My Password – Attempts to brute-force a password by generating candidate strings and comparing their SHA-256 hashes against the target.

Generate Random Passwords – Generates a list of strong, random passwords using a configurable character set, including special characters.

Each mode provides a clean, responsive interface with real-time logging, user feedback, and interactive control over parameters like password length and generation count.

Features and Design
1. Brute-Force Cracking (Crack My Password Mode)
SHA-256 Hashing: When the user inputs a password, the application immediately computes its SHA-256 hash. The cracking process never sees the plain text—only the hash.

Recursive Search: The application uses a recursive depth-first search algorithm to generate every possible string combination (a-z, A-Z, 0-9) up to a user-defined max length.

Multithreaded Execution: The cracking process runs on a background SwingWorker thread, keeping the UI responsive. Progress is reported live through a progress bar, speed tracker (attempts/sec), and an estimated time remaining display.

Real-World Use Case Simulation: This feature could hypothetically be used in scenarios like recovering forgotten passwords from hashed values or analyzing password strength in controlled environments.

Note: The cracking logic does not include special characters for performance reasons. Including them would drastically increase the search space.

2. Password Generation (Generate Random Passwords Mode)
Customizable Output: Users can specify how many passwords to generate and whether to include special characters.

Cryptographically Secure Randomness: Passwords are generated using SecureRandom, making them suitable for use in secure systems (within the limits of a demo app).

Easy Export: Generated passwords can be saved to a .txt file using the built-in export feature. This is useful in a variety of scenarios, such as:

Pre-generating secure credentials for users or devices

Transferring passwords for external storage or testing

Passing data to another system that performs cracking offline (for example, importing the file into a GPU-accelerated cracking tool)

3. Export Capability
In either mode (but especially in generation mode), users can export results using the "Export All" option. The export functionality opens a directory chooser to allow writing the generated password list to a USB drive or any location on the file system.

In a hypothetical real-world scenario, a security researcher could use this mode to:

Generate a list of candidate passwords

Export them to a portable drive

Move them to a more powerful cracking system (e.g., using Hashcat on a GPU rig)

Compare hashes offline to reduce CPU burden on their main machine

This approach mirrors workflows used in penetration testing or digital forensics, where password hashes might be obtained from a compromised system and need to be tested elsewhere.

Technical Stack
Language: Java

GUI Toolkit: Swing (JFrame, JPanel, JTextArea, etc.)

Security: SHA-256 hashing via MessageDigest, randomness via SecureRandom

Multithreading: SwingWorker for background tasks

Layout: GridBagLayout for flexible input arrangement

The project focuses on readability and interactivity rather than performance. For educational purposes, everything is handled within Java and intentionally avoids native optimizations or parallel GPU work.

Building & Running
Prerequisites
Java JDK 8 or later

Compile
bash
Copy
Edit
javac PasswordCracker.java
Run
bash
Copy
Edit
java PasswordCracker
Once running, the application presents an interactive GUI where you can select a mode and configure inputs.

Limitations
Password Cracking: This app is not optimized for speed or large character sets. It is intentionally limited to simple brute-force to demonstrate the concept.

Hash Algorithms: Only SHA-256 is supported. No rainbow tables or salting mechanisms are implemented.

Charset: Cracking uses a-z, A-Z, and 0-9 only. Password generation allows more flexibility with special characters.

Future Improvements
Parallel cracking threads with timeout control

Support for importing hashed values from files

Selectable hash algorithms (MD5, SHA-1, bcrypt, etc.)

Adjustable generated password length

Dark mode or modern UI frameworks like JavaFX

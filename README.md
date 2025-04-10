# PasswordCracker

**PasswordCracker** is a simple Java desktop app with a GUI that lets you either generate random passwords or try to brute-force crack a password you enter. It’s built with Java Swing and meant to be both educational and functional. 

It shows how brute-force password cracking works under the hood, using hashing and recursion, and also includes a basic password generator with export capability.

---

## What It Does

### Crack My Password Mode

- You type in a password, the app hashes it using SHA-256.
- It then starts guessing strings (`a-z`, `A-Z`, `0-9`) recursively until it finds one that matches the hash.
- You can set a max password length so it doesn’t run forever.
- It shows live stats like how fast it’s guessing and an estimate of how long it might take.

This is mainly for demonstrating how brute-force actually works — it’s not optimized or fast like a real cracking tool, and it won’t handle long or complex passwords well.

---

### Generate Random Passwords Mode

- Generates a list of secure random passwords.
- You can choose how many to generate and whether or not to include special characters.
- Uses `SecureRandom`, so the generated passwords are strong and not predictable.

Good for testing, temporary credentials, or just having a quick way to grab some secure passwords.

---

### Export Feature

There’s a built-in export option to save all the generated passwords to a `.txt` file. You choose where to save — like a folder or USB drive.

In a real-world scenario, you might use this to:
- Create a list of passwords to try cracking later with a more powerful tool
- Move candidate passwords to another machine for offline testing
- Store generated credentials for provisioning systems or audits

---

## How It Works (Under the Hood)

- The GUI is built with plain Java Swing.
- The cracking function is recursive, trying every combination of characters up to the length you specify.
- It runs in the background using a `SwingWorker`, so the app doesn’t freeze while cracking.
- Password hashing is done with Java’s `MessageDigest` and SHA-256.
- Random passwords are created with `SecureRandom` to ensure they’re not predictable.

---

## Running It

### Requirements

- Java JDK 8 or higher

### Compile

```bash
javac PasswordCracker.java
```

### Run

```bash
java PasswordCracker
```

---

## Limitations

- Cracking is limited to `a-z`, `A-Z`, and `0-9`. No special characters (on purpose, to keep it from being too slow).
- It’s not meant to be a real hacking tool — more of a demonstration.
- Only supports SHA-256 right now.
- No salting or advanced hash comparison.

---

## Possible Improvements

Stuff that could be added later:

- Multiple threads for faster cracking
- Support for importing password hashes from a file
- Bcrypt, SHA-1, or MD5 hash support
- A dark mode or better UI with JavaFX
- Option to set generated password length

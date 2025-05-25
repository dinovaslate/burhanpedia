package com.burhanpedia.core.menu;

import com.burhanpedia.Burhanpedia;
import com.burhanpedia.core.command.SystemCommand;
import com.burhanpedia.core.interfaces.SystemMenu;
import com.burhanpedia.exception.*;
import com.burhanpedia.model.user.*;
import com.burhanpedia.service.SystemAdmin;
import com.burhanpedia.service.SystemPembeli;
import com.burhanpedia.service.SystemPengirim;
import com.burhanpedia.service.SystemPenjual;
import com.burhanpedia.util.BurhanpediaLogger;
import com.burhanpedia.util.DateManager;

import java.util.Scanner;
import java.util.Optional;

public class MainMenuSystem implements SystemMenu {
    private SystemPembeli systemPembeli;
    private SystemPenjual systemPenjual;
    private SystemPengirim systemPengirim;
    private SystemAdmin systemAdmin;
    private Scanner input;
    private Burhanpedia mainRepository;
    private SystemCommand systemCommand;
    private BurhanpediaLogger logger;
    
    public MainMenuSystem() {
        this.input = new Scanner(System.in);
        this.mainRepository = new Burhanpedia();
        initializeSystems();
    }
    
    // Added constructor to accept an existing Burhanpedia instance
    public MainMenuSystem(Burhanpedia burhanpedia) {
        this.input = new Scanner(System.in);
        this.mainRepository = burhanpedia;
        initializeSystems();
    }
    
    // Extracted initialization logic to a separate method for code reuse
    private void initializeSystems() {
        this.systemPembeli = new SystemPembeli(input, mainRepository);
        this.systemPenjual = new SystemPenjual(input, mainRepository);
        this.systemPengirim = new SystemPengirim(input, mainRepository);
        this.systemAdmin = new SystemAdmin(input, mainRepository);
        this.systemCommand = new SystemCommand(input);
        this.logger = BurhanpediaLogger.getInstance();
    }
    
    @Override
    public String showMenu() {
        return "\n╔════════════════════════════════════╗\n" +
               "║           MENU UTAMA               ║\n" +
               "╠════════════════════════════════════╣\n" +
               "║ 1. Login                           ║\n" +
               "║ 2. Register                        ║\n" +
               "║ 3. Hari Selanjutnya                ║\n" +
               "║ 4. Command                         ║\n" +
               "║ 5. Keluar                          ║\n" +
               "╚════════════════════════════════════╝";
    }
    
    public void printWelcomeBanner() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                          ║");
        System.out.println("║  ____             _                   _____         _ _  ║");
        System.out.println("║ |  _ \\           | |                 |  __ \\       | (_) ║");
        System.out.println("║ | |_) |_   _ _ __| |__   __ _ _ __   | |__) |__  __| |_  ║");
        System.out.println("║ |  _ <| | | | '__| '_ \\ / _` | '_ \\  |  ___/ _ \\/ _` | | ║");
        System.out.println("║ | |_) | |_| | |  | | | | (_| | | | | | |  |  __/ (_| | | ║");
        System.out.println("║ |____/ \\__,_|_|  |_| |_|\\__,_|_| |_| |_|   \\___|\\__,_|_| ║");
        System.out.println("║                                                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║             Selamat datang di Burhanpedia!                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }
    
    @Override
    public void handleMenu() {
        boolean isRunning = true;
        printWelcomeBanner();
        while (isRunning) {
            System.out.println(showMenu());
            System.out.print("Perintah : ");
            String command = input.nextLine();
            
            try {
                switch (command) {
                    case "1":
                        try {
                            logger.log("MainMenu", "Memilih menu login");
                            handleLogin();
                        } catch (LoginException e) {
                            logger.log("Error", "Login gagal: " + e.getMessage());
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case "2":
                        try {
                            logger.log("MainMenu", "Memilih menu registrasi");
                            handleRegister();
                        } catch (RegistrationException e) {
                            logger.log("Error", "Registrasi gagal: " + e.getMessage());
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case "3":
                        try {
                            logger.log("MainMenu", "Memilih menu hari selanjutnya");
                            handleNextDay();
                        } catch (TransactionProcessException e) {
                            logger.log("Error", "Proses hari selanjutnya gagal: " + e.getMessage());
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    case "4":
                        logger.log("MainMenu", "Memilih menu command");
                        handleCommand();
                        break;
                    case "5":
                        logger.log("MainMenu", "Keluar dari aplikasi");
                        isRunning = false;
                        System.out.println("Terima kasih telah menggunakan Burhanpedia!");
                        break;
                    default:
                        logger.log("Error", "Perintah tidak valid: " + command);
                        System.out.println("Perintah tidak valid!");
                }
            } catch (Exception e) {
                logger.log("Error", "Terjadi kesalahan yang tidak terduga: " + e.getMessage());
                System.out.println("Error: Terjadi kesalahan yang tidak terduga - " + e.getMessage());
            }
        }
    }
    
    private void handleCommand() {
        systemCommand.handleCommand();
    }
    
    private void handleLogin() throws LoginException {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║              LOGIN                 ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan username: ");
        String username = input.nextLine();
        System.out.print("Masukkan password: ");
        String password = input.nextLine();
        
        logger.log("Login", "Percobaan login dengan username: " + username);
        
        // Check for admin login
        if (validateAdminLogin(username, password)) {
            logger.log("Login", "Login admin berhasil: " + username);
            System.out.printf("\n✓ Login berhasil! Selamat datang, %s!%n", username);
            systemAdmin.setActiveAdmin(mainRepository.getAdminRepo().getUserByName(username));
            systemAdmin.handleMenu();
            return;
        }
        
        // Check for user login
        String[] roles = mainRepository.getUserRepo().getUserRoles(username);
        
        if (roles.length == 0) {
            logger.log("Login", "Username tidak ditemukan: " + username);
            throw new LoginException("Username tidak ditemukan!");
        }
        
        // Validate password
        Optional<User> userOptional = mainRepository.getUserRepo().getUserByName(username);
        if (userOptional.isEmpty() || !userOptional.get().getPassword().equals(password)) {
            logger.log("Login", "Password salah untuk username: " + username);
            throw new LoginException("Password salah!");
        }
        
        // If user has multiple roles, ask which one to use
        if (roles.length > 1) {
            logger.log("Login", "User " + username + " memiliki multiple roles");
            handleMultipleRoleLogin(username, roles);
        } else {
            // Single role login
            String role = roles[0];
            logger.log("Login", "Login berhasil sebagai " + role + ": " + username);
            
            switch (role) {
                case "Pembeli":
                    systemPembeli.setActivePembeli((Pembeli) userOptional.get());
                    System.out.println("Login berhasil! Selamat datang, " + username + "!");
                    systemPembeli.handleMenu();
                    break;
                case "Penjual":
                    systemPenjual.setActivePenjual((Penjual) userOptional.get());
                    System.out.println("Login berhasil! Selamat datang, " + username + "!");
                    systemPenjual.handleMenu();
                    break;
                case "Pengirim":
                    systemPengirim.setActivePengirim((Pengirim) userOptional.get());
                    System.out.println("Login berhasil! Selamat datang, " + username + "!");
                    systemPengirim.handleMenu();
                    break;
            }
        }
    }
    
    private boolean validateAdminLogin(String username, String password) {
        var admin = mainRepository.getAdminRepo().getUserByName(username);
        return admin != null && admin.getPassword().equals(password);
    }
    
    private void handleMultipleRoleLogin(String username, String[] roles) {
        boolean validChoice = false;
        
        while (!validChoice) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         PILIH ROLE LOGIN          ║");
            System.out.println("╠════════════════════════════════════╣");
            
            int index = 1;
            for (String role : roles) {
                System.out.printf("║ %d. %-30s ║%n", index, role);
                index++;
            }
            
            System.out.printf("║ %d. Cek Saldo Antar Role            ║%n", index);
            System.out.printf("║ %d. Exit                            ║%n", index + 1);
            System.out.println("╚════════════════════════════════════╝");
            
            System.out.print("Perintah : ");
            String choice = input.nextLine();
            
            try {
                int option = Integer.parseInt(choice);
                
                if (option >= 1 && option <= roles.length) {
                    String selectedRole = roles[option - 1];
                    User user = null;
                    
                    for (User u : mainRepository.getUserRepo().getAll()) {
                        if (u.getUsername().equals(username) && u.getRole().equals(selectedRole)) {
                            user = u;
                            break;
                        }
                    }
                    
                    if (user != null) {
                        logger.log("Login", "Login berhasil sebagai " + selectedRole + ": " + username);
                        
                        switch (selectedRole) {
                            case "Pembeli":
                                systemPembeli.setActivePembeli((Pembeli) user);
                                System.out.println("Login berhasil! Selamat datang, " + username + "!");
                                systemPembeli.handleMenu();
                                break;
                            case "Penjual":
                                systemPenjual.setActivePenjual((Penjual) user);
                                System.out.println("Login berhasil! Selamat datang, " + username + "!");
                                systemPenjual.handleMenu();
                                break;
                            case "Pengirim":
                                systemPengirim.setActivePengirim((Pengirim) user);
                                System.out.println("Login berhasil! Selamat datang, " + username + "!");
                                systemPengirim.handleMenu();
                                break;
                        }
                        validChoice = true;
                    }
                } else if (option == roles.length + 1) {
                    logger.log("Login", "User " + username + " memeriksa saldo antar akun");
                    try {
                        handleCekSaldoAntarAkun(username);
                    } catch (UnknownUserException e) {
                        logger.log("Error", "Gagal cek saldo: " + e.getMessage());
                        System.out.println(e.getMessage());
                    }
                    validChoice = true;
                } else if (option == roles.length + 2) {
                    logger.log("Login", "User " + username + " membatalkan login");
                    System.out.println("Login dibatalkan, kembali ke menu utama...");
                    validChoice = true;
                } else {
                    logger.log("Error", "Pilihan tidak valid: " + option);
                    System.out.println("Pilihan tidak valid!");
                }
            } catch (NumberFormatException e) {
                logger.log("Error", "Input bukan angka: " + choice);
                System.out.println("Masukkan angka yang valid!");
            }
        }
    }
    
    private void handleCekSaldoAntarAkun(String username) throws UnknownUserException {
        boolean userFound = false;
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║         SALDO ANTAR AKUN          ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ Role        │ Saldo                ║");
        System.out.println("╠════════════════════════════════════╣");
        
        for (User user : mainRepository.getUserRepo().getAll()) {
            if (user.getUsername().equals(username)) {
                System.out.printf("║ %-10s │ %-20.2f ║%n", 
                    user.getRole(), 
                    user.getBalance() / 100.0);
                userFound = true;
            }
        }
        
        if (!userFound) {
            logger.log("Error", "User tidak ditemukan: " + username);
            throw new UnknownUserException("User dengan username " + username + " tidak ditemukan");
        }
        
        logger.log("Saldo", "User " + username + " melihat saldo antar akun");
        System.out.println("╚════════════════════════════════════╝");
    }
    
    private void handleRegister() throws RegistrationException {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           REGISTRASI              ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Masukkan username: ");
        String username = input.nextLine();
        
        logger.log("Register", "Percobaan registrasi dengan username: " + username);
        
        // Check if username exists
        String[] existingRoles = mainRepository.getUserRepo().getUserRoles(username);
        
        if (existingRoles.length > 0) {
            // Existing user logic - need to get password from existing user
            logger.log("Register", "User sudah ada: " + username);
            System.out.println("Username sudah terdaftar. Mendaftarkan role baru...");
            
            // Get password from existing user
            Optional<User> existingUser = mainRepository.getUserRepo().getUserByName(username);
            if (existingUser.isPresent()) {
                String password = existingUser.get().getPassword();
                registerRole(username, password, existingRoles);
            } else {
                logger.log("Error", "User terdeteksi ada tetapi tidak ditemukan: " + username);
                throw new RegistrationException("Error sistem: User tidak ditemukan");
            }
        } else {
            // New user registration
            logger.log("Register", "Registrasi user baru: " + username);
            System.out.print("Masukkan password: ");
            String password = input.nextLine();
            registerRole(username, password, new String[0]);
        }
    }
    
    private void registerRole(String username, String password, String[] existingRoles) {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           PILIH ROLE              ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Penjual                        ║");
        System.out.println("║ 2. Pembeli                        ║");
        System.out.println("║ 3. Pengirim                       ║");
        System.out.println("║ 4. Batalkan register              ║");
        System.out.println("╚════════════════════════════════════╝");
        
        System.out.print("Perintah : ");
        String choice = input.nextLine();
        
        try {
            int option = Integer.parseInt(choice);
            String newRole;
            
            switch (option) {
                case 1:
                    newRole = "Penjual";
                    break;
                case 2:
                    newRole = "Pembeli";
                    break;
                case 3:
                    newRole = "Pengirim";
                    break;
                case 4:
                    logger.log("Register", "User " + username + " membatalkan registrasi");
                    System.out.println("Registrasi dibatalkan, kembali ke menu utama...");
                    return;
                default:
                    logger.log("Error", "Pilihan role tidak valid: " + option);
                    System.out.println("Pilihan tidak valid!");
                    return;
            }
            
            // Check if role already exists
            for (String role : existingRoles) {
                if (role.equals(newRole)) {
                    logger.log("Register", "Role " + newRole + " sudah ada untuk user " + username);
                    System.out.println("Role sudah ada. Silahkan pilih role lain!");
                    return;
                }
            }
            
            // Register new role
            switch (newRole) {
                case "Penjual":
                    System.out.print("Masukkan nama toko: ");
                    String namaToko = input.nextLine();
                    mainRepository.getUserRepo().addUser(new Penjual(username, password, namaToko));
                    logger.log("Register", "Registrasi penjual berhasil: " + username + " (Toko: " + namaToko + ")");
                    System.out.println("Registrasi akun penjual berhasil!");
                    break;
                case "Pembeli":
                    mainRepository.getUserRepo().addUser(new Pembeli(username, password));
                    logger.log("Register", "Registrasi pembeli berhasil: " + username);
                    System.out.println("Registrasi akun pembeli berhasil!");  // This line should be executed for Pembeli
                    break;
                case "Pengirim":
                    mainRepository.getUserRepo().addUser(new Pengirim(username, password));
                    logger.log("Register", "Registrasi pengirim berhasil: " + username);
                    System.out.println("Registrasi akun pengirim berhasil!");
                    break;
            }
            
        } catch (NumberFormatException e) {
            logger.log("Error", "Input bukan angka: " + choice);
            System.out.println("Masukkan angka yang valid!");
        }
    }
    
    private void handleNextDay() throws TransactionProcessException {
        try {
            logger.log("System", "Memproses hari selanjutnya");
            // Process all active transactions
            mainRepository.getTransaksiRepo().prosesTransaksi();
            
            // Update date using DateManager
            DateManager dateManager = DateManager.getInstance();
            dateManager.nextDay();
            
            // Show updated date
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║         HARI SELANJUTNYA          ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.printf("║ Tanggal: %-25s ║%n", dateManager.getCurrentDateFormatted());
            System.out.println("║ Pok pok pok!                       ║");
            System.out.println("╚════════════════════════════════════╝");
            
            logger.log("System", "Hari selanjutnya berhasil diproses");
        } catch (Exception e) {
            logger.log("Error", "Gagal memproses hari selanjutnya: " + e.getMessage());
            throw new TransactionProcessException("Gagal memproses transaksi: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        MainMenuSystem mainMenu = new MainMenuSystem();
        mainMenu.handleMenu();
    }
}
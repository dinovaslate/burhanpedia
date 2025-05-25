package com.burhanpedia.repository;

import com.burhanpedia.model.user.*;
import com.burhanpedia.util.CsvReader;
import com.burhanpedia.util.CsvWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());
    private static final String CSV_DIRECTORY = "data";
    private static final String USER_CSV_FILE = CSV_DIRECTORY + File.separator + "users.csv";
    
    private final List<User> userList;
    
    public UserRepository() {
        userList = new ArrayList<>();
        loadUsersFromCsv();
    }
    
    /**
     * Adds a user to the repository and saves the updated data to CSV.
     * 
     * @param user The user to add
     */
    public void addUser(User user) {
        userList.add(user);
        saveUsersToCsv();
    }
    
    public Optional<User> getUserById(UUID id) {
        return userList.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> getUserByName(String username) {
        return userList.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }
    
    public User[] getAll() {
        return userList.toArray(new User[0]);
    }
    
    public String[] getUserRoles(String username) {
        return userList.stream().filter(u -> u.getUsername().equals(username)).map(User::getRole).toArray(String[]::new);
    }
    
    /**
     * Updates a user in the repository and saves changes to CSV.
     * 
     * @param user The user to update
     * @return true if update was successful, false if user was not found
     */
    public boolean updateUser(User user) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(user.getId())) {
                userList.set(i, user);
                saveUsersToCsv();
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Removes a user from the repository and updates the CSV.
     * 
     * @param userId The ID of the user to remove
     * @return true if removal was successful, false if user was not found
     */
    public boolean removeUser(UUID userId) {
        Iterator<User> it = userList.iterator();
        while (it.hasNext()) {
            User u = it.next();
            if (u.getId().equals(userId)) {
                it.remove();
                saveUsersToCsv();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Loads all users from the CSV file into the repository.
     */
    private void loadUsersFromCsv() {
        File file = new File(USER_CSV_FILE);
        if (!file.exists()) {
            File dir = new File(CSV_DIRECTORY);
            if (!dir.exists() && !dir.mkdirs()) {
            }
            return;
        }
        
        try (CsvReader reader = new CsvReader(USER_CSV_FILE)) {
            List<String> header = reader.readNext();
            if (header == null) {
                return;
            }
            
            List<String> row;
            while ((row = reader.readNext()) != null) {
                try {
                    User user = parseUserFromRow(row);
                    if (user != null) userList.add(user);
                } catch (Exception e) {
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading users from CSV", e);
        }
    }
    
    /**
     * Saves all users in the repository to the CSV file.
     */
    private void saveUsersToCsv() {
        try (CsvWriter writer = new CsvWriter(USER_CSV_FILE)) {
            writer.writeHeader(List.of("id", "role", "username", "password", "balance", "namaToko"));
            for (User user : userList) {
                writer.writeRow(userToCsvRow(user));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving users to CSV", e);
        }
    }
    
    /**
     * Creates a User object from a CSV row.
     * 
     * @param row The CSV row data
     * @return A User object or null if the row is invalid
     */
    private User parseUserFromRow(List<String> row) {
        if (row.size() < 5) return null;
        try {
            String id = row.get(0);
            String role = row.get(1);
            String username = row.get(2);
            String password = row.get(3);
            long balance = Long.parseLong(row.get(4));
            User user;
            switch (role) {
                case "Pembeli":
                    user = new Pembeli(username, password);
                    user.setBalance(balance);
                    break;
                case "Penjual":
                    String namaToko = row.size() > 5 ? row.get(5) : "";
                    user = new Penjual(username, password, namaToko);
                    user.setBalance(balance);
                    break;
                case "Pengirim":
                    user = new Pengirim(username, password);
                    user.setBalance(balance);
                    break;
                default:
                    return null;
            }
            
            setField(user, "id", UUID.fromString(id));
            
            return user;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void setField(User user, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(user, value);
        } catch (Exception e) {
        }
    }
    
    /**
     * Creates a CSV row from a User object.
     * 
     * @param user The User object
     * @return 
     */
    private List<String> userToCsvRow(User user) {
        List<String> row = new ArrayList<>();
        row.add(user.getId().toString());
        row.add(user.getRole());
        row.add(user.getUsername());
        row.add(user.getPassword());
        row.add(String.valueOf(user.getBalance()));
        row.add(user instanceof Penjual ? ((Penjual) user).getNamaToko() : "");
        return row;
    }

    public void clear() {
        userList.clear();
        saveUsersToCsv();
        
    }
}
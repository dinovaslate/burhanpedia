package com.burhanpedia.repository;

import com.burhanpedia.model.user.Admin;
import java.util.*;

public class AdminRepository {
    private final List<Admin> adminList;
    
    public AdminRepository() {
        this.adminList = new ArrayList<>();
        // Add default admin
        adminList.add(new Admin("admin", "admin123"));
    }
    
    public Admin getUserByName(String username) {
        if (username == null) return null;
        return adminList.stream()
                .filter(admin -> username.equals(admin.getUsername()))
                .findFirst()
                .orElse(null);
    }
    
    public Admin[] getAll() {
        return adminList.toArray(new Admin[0]);
    }

    public void addAdmin(Admin admin) {
        if (admin != null && getUserByName(admin.getUsername()) == null) {
            adminList.add(admin);
        }
    }
}
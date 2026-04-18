package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    
    // Count methods for admin stats
    Long countByRole(Role role);
    Long countByRoleAndIsActive(Role role, boolean isActive);
    
    // Search members for admin
    @Query("SELECT u FROM User u WHERE u.role = 'MEMBER' AND " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchMembers(@Param("search") String search);
    
    // Get all members
    @Query("SELECT u FROM User u WHERE u.role = 'MEMBER'")
    List<User> findAllMembers();
    
    // Find users with overdue books
    @Query("SELECT DISTINCT u FROM User u JOIN Transaction t ON u.id = t.user.id " +
           "WHERE t.status = 'ISSUED' AND t.dueDate < CURRENT_DATE")
    List<User> findUsersWithOverdueBooks();
}

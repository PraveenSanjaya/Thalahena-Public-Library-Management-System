package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Notification;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // User-specific notifications
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    // Find by user ID
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find all ordered
    List<Notification> findAllByOrderByCreatedAtDesc();
    
    // Count unread
    Long countByUserIdAndIsReadFalse(Long userId);
    
    // Find broadcast notifications (user_id is null)
    @Query("SELECT n FROM Notification n WHERE n.user IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findBroadcastNotifications();
    
    // Search by title or message
    @Query("SELECT n FROM Notification n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%')) ORDER BY n.createdAt DESC")
    List<Notification> searchByTitleOrMessage(String search);

    // Fetch member-specific + broadcast notifications
    @Query("SELECT n FROM Notification n WHERE n.user IS NULL OR n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findMemberNotifications(@org.springframework.data.repository.query.Param("userId") Long userId);
}

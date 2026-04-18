package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Feedback;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUser(User user);
    
    // Get recent feedback
    List<Feedback> findTop5ByOrderByCreatedAtDesc();
}

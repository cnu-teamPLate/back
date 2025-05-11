package com.cnu.teamProj.teamProj.review;

import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    ArrayList<Review> findReviewsByUserId(User userId);
}

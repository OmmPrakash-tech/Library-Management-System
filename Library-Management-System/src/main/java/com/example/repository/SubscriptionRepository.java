package com.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.model.Subscription;
import com.example.model.User;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{

    @Query("select s from Subscription s where s.user.id = :userId AND " +
       "s.isActive = true and " +
       "s.startDate <= :today and s.endDate >= :today")
Optional<Subscription> findActiveSubscriptionByUserId(
        @Param("userId") Long userId,
        @Param("today") LocalDate today
);

@Query("select s from Subscription s where s.isActive = true " +
       "AND s.endDate < :today")
List<Subscription> findExpiredActiveSubscriptions(
        @Param("today") LocalDate today
);

Optional<Subscription> findByUserAndIsActiveTrue(User user);

List<Subscription> findByUserIdAndIsActiveTrue(Long userId);

List<Subscription> findByIsActiveTrue();

@Query("SELECT s FROM Subscription s WHERE s.endDate < CURRENT_DATE")
Page<Subscription> findInactiveSubscriptions(Pageable pageable);

@Query("""
SELECT s FROM Subscription s
WHERE s.endDate < CURRENT_DATE
AND s.startDate = (
    SELECT MAX(s2.startDate)
    FROM Subscription s2
    WHERE s2.user.id = s.user.id
)
""")
Page<Subscription> findLatestInactiveSubscriptions(Pageable pageable);

}

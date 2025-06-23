package siwbooks.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import siwbooks.siwbooks.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByBookIdOrderByCreatedAtDesc(Long bookId);
    
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);
    
    boolean existsByBookIdAndUserId(Long bookId, Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
    
    long countByBookId(Long bookId);
} 
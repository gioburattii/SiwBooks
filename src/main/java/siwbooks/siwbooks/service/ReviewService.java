package siwbooks.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import siwbooks.siwbooks.model.Review;
import siwbooks.siwbooks.model.Book;
import siwbooks.siwbooks.model.User;
import siwbooks.siwbooks.repository.ReviewRepository;
import siwbooks.siwbooks.repository.BookRepository;
import siwbooks.siwbooks.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }
    
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }
    
    public List<Review> findByBookId(Long bookId) {
        return reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId);
    }
    
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public Optional<Review> findByBookIdAndUserId(Long bookId, Long userId) {
        return reviewRepository.findByBookIdAndUserId(bookId, userId);
    }
    
    public boolean hasUserReviewedBook(Long bookId, Long userId) {
        return reviewRepository.existsByBookIdAndUserId(bookId, userId);
    }
    
    public Double getAverageRatingForBook(Long bookId) {
        return reviewRepository.findAverageRatingByBookId(bookId);
    }
    
    public long getReviewCountForBook(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }
    
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
    
    public Review createReview(String title, Integer rating, String content, Long bookId, Long userId) {
        Optional<Book> book = bookRepository.findById(bookId);
        Optional<User> user = userRepository.findById(userId);
        
        if (book.isEmpty()) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        // Check if user has already reviewed this book
        if (hasUserReviewedBook(bookId, userId)) {
            throw new RuntimeException("User has already reviewed this book");
        }
        
        Review review = new Review(title, rating, content, book.get(), user.get());
        return save(review);
    }
    
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return reviewRepository.existsById(id);
    }
}
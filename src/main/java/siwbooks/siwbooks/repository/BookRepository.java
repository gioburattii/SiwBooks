package siwbooks.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import siwbooks.siwbooks.model.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByPublicationYear(Integer year);
    
    List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);
    
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE " +
           "LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorName(@Param("authorName") String authorName);
    
    @Query("SELECT b FROM Book b WHERE b.id IN " +
           "(SELECT r.book.id FROM Review r GROUP BY r.book.id ORDER BY AVG(r.rating) DESC)")
    List<Book> findBooksOrderedByAverageRating();
} 
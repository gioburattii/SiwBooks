package siwbooks.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import siwbooks.siwbooks.model.Author;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    List<Author> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String firstName, String lastName);
    
    List<Author> findByNationalityIgnoreCase(String nationality);
    
    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> findByFullNameContaining(@Param("name") String name);
} 
package id.ac.ui.cs.advprog.yomureadingservice.reading.repository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
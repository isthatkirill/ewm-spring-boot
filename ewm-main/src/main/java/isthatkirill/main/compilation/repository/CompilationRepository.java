package isthatkirill.main.compilation.repository;

import isthatkirill.main.compilation.model.Compilation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findAllByPinnedIs(Boolean pinned, Pageable pageable);

}

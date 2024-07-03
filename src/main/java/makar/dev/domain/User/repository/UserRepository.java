package makar.dev.domain.User.repository;

import makar.dev.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

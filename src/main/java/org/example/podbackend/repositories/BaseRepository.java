package org.example.podbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<E, ID> extends JpaRepository<E, ID> {
  Optional<E> findById(ID id);
}

package com.ice.repository;

import com.ice.entity.ArtistAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistAliasRepository extends JpaRepository<ArtistAlias, Long> {
}

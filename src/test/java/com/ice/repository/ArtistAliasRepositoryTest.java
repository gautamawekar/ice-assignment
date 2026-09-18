package com.ice.repository;

import com.ice.entity.ArtistAlias;
import com.ice.entity.ArtistProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArtistAliasRepositoryTest {

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private ArtistAliasRepository artistAliasRepository;

    @Test
    void saveAliasForArtist() {
        ArtistProfile profile = new ArtistProfile("Alias Artist");
        ArtistProfile saved = artistProfileRepository.save(profile);

        ArtistAlias alias = new ArtistAlias(saved, "A.K.A. Alias");
        ArtistAlias savedAlias = artistAliasRepository.save(alias);

        assertThat(savedAlias.getAliasId()).isNotNull();
        assertThat(savedAlias.getCreatedAt()).isNotNull();
        assertThat(savedAlias.getArtist().getArtistId()).isEqualTo(saved.getArtistId());
    }
}

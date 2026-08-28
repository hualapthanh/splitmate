package com.pm.groupservice.repository;

import com.pm.groupservice.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    Optional<Group> findByInviteCode(String inviteCode);

    @Query("SELECT DISTINCT g FROM Group g JOIN g.members m WHERE m.userId = :userId AND m.status = 'ACTIVE' AND g.status = 'ACTIVE'")
    List<Group> findAllByUserId(@Param("userId") UUID userId);
}

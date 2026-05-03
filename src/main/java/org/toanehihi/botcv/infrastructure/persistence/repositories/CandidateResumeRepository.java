package org.toanehihi.botcv.infrastructure.persistence.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.toanehihi.botcv.domain.model.CandidateResume;

import java.util.List;

@Repository
public interface CandidateResumeRepository extends JpaRepository<CandidateResume, Long> {
    List<CandidateResume> findByCandidateId(Long candidateId);
    Page<CandidateResume> findByCandidateId(Long candidateId, Pageable pageable);
}

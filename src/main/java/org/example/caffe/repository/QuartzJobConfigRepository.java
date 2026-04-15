package org.example.caffe.repository;

import org.example.caffe.domain.QuartzJobConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuartzJobConfigRepository extends JpaRepository<QuartzJobConfig, Long> {
    Optional<QuartzJobConfig> findByJobName(String jobName);
}

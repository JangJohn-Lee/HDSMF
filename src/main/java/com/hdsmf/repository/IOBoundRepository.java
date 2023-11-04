package com.hdsmf.repository;

import com.hdsmf.entity.Components;
import com.hdsmf.entity.IOBound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOBoundRepository extends JpaRepository<IOBound, Long> {

    Optional<IOBound> findByRackDetailNo(Long rackDetailNo);

}
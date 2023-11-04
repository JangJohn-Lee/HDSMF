package com.hdsmf.repository;

import com.hdsmf.entity.RackInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RackInfoRepository extends JpaRepository<RackInfo, Long> {

}
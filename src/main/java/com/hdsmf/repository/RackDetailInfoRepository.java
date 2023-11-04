package com.hdsmf.repository;


import com.hdsmf.entity.RackDetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RackDetailInfoRepository extends JpaRepository<RackDetailInfo, Long> {

}
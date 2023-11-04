package com.hdsmf.repository;

import com.hdsmf.entity.IOBound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Repository
public interface OutBoundRepository extends JpaRepository<IOBound, Integer> {

    //출고 시 part_no 검색 → 포함된 리스트 출력
    @Query(value = "SELECT i FROM IOBound i WHERE i.componentNo LIKE CONCAT('%', :partNo, '%')")
    List<IOBound> selectIOBoundList(@Param("partNo") String partNo);

    //rack detail no으로 rack 위치 출력
    @Query(value = "SELECT r.rackId FROM RackDetailInfo r WHERE r.rackDetailNo = :rackNo")
    String selectRackLocation(@Param("rackNo") long rackNo);

    //출고 처리
    @Modifying
    @Query(value = "DELETE FROM IOBound i WHERE i.rackDetailNo = :rackNo")
    void deleteRack(@Param("rackNo") long rackNo);

    //품번 실시간 검색
//    @Query("SELECT DISTINCT SUBSTRING(c.componentNo, 1, LOCATE('-', c.componentNo) - 1) " +
//            "FROM IOBound c " +
//            "WHERE c.componentNo like concat('%', :componentNo, '%') " +
//            "OR (LOCATE('-', c.componentNo) = 0 AND c.componentNo like concat('%', :componentNo, '%'))")
//    List<String> OutComponentList(@Param("componentNo") String componentNo);

    @Query("SELECT DISTINCT c.componentNo " +
            "FROM IOBound c " +
            "WHERE c.componentNo like concat('%', :componentNo, '%') ")
    List<String> OutComponentList(@Param("componentNo") String componentNo);

}
package com.hdsmf.repository;

import com.hdsmf.dto.RackDetailSearchDto;
import com.hdsmf.entity.RackDetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RackRepository extends JpaRepository<RackDetailInfo, Long> {

    //Rack별 row의 개수
    @Query(value = "SELECT count(distinct (r.rowNo)) FROM RackDetailInfo r WHERE r.rackNo = :rackNo")
    Integer selectRowNo(@Param("rackNo") long rackNo);

    //Rack별 column의 개수
    @Query(value = "SELECT count(distinct (r.columnNo)) FROM RackDetailInfo r WHERE r.rackNo = :rackNo")
    Integer selectColNo(@Param("rackNo") long rackNo);

    //Rack 총 개수
    @Query(value = "SELECT count(r.rackName) FROM RackInfo r")
    Integer selectRackNo();

    // Category와 RackDetailInfo 조인하여 중복된 값을 제거하고 categoryname 출력
    @Query("SELECT DISTINCT c.categoryName " +
            "FROM RackDetailInfo rdi " +
            "JOIN Category c ON rdi.categoryNo = c.categoryNo " +
            "WHERE rdi.rackNo = :rackNo")
    List<String> findDistinctCategoryNamesByRackNo(@Param("rackNo") Long rackNo);

    @Query(value = "SELECT new com.hdsmf.dto.RackDetailSearchDto(rdi.rackId, " +
            "rdi.rackDetailNo, rdi.rowNo, rdi.columnNo, c.categoryNo, c.categoryName, c.categoryColor, iob.componentAmount, " +
            "iob.inboundDate, co.componentNo, co.product, co.model, co.componentState) " +
            "FROM RackDetailInfo rdi " +
            "LEFT JOIN Category c ON rdi.categoryNo = c.categoryNo " +
            "LEFT JOIN IOBound iob ON rdi.rackDetailNo = iob.rackDetailNo " +
            "LEFT JOIN Components co ON iob.componentNo = co.componentNo " +
            "WHERE rdi.rackNo = :rackNo ")
    List<RackDetailSearchDto> findByRackNoWithJoins(@Param("rackNo") Long rackNo);
    
    //RackId로 매칭되는 rackDetailNo 찾기
    Optional<RackDetailInfo> findByRackId(String rackId);
}

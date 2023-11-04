package com.hdsmf.repository;

import com.hdsmf.dto.IOBounds;

import com.hdsmf.entity.IOBound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InBoundRepository extends JpaRepository<IOBound, String> {
    @Query("SELECT io.inboundDate AS inboundDate" +
            ", io.componentNo AS componentNo" +
            ", c.product AS product" +
            ", io.componentAmount AS componentAmount" +
            ", c.componentState AS componentState" +
            ", r.rackId AS rackId " +
            "FROM IOBound io " +
            "JOIN Components c ON c.componentNo = io.componentNo " +
            "JOIN RackDetailInfo r ON r.rackDetailNo = io.rackDetailNo " +
            "WHERE c.componentNo LIKE CONCAT('%', :componentNo, '%') " + //품번 입력
            "AND (:rackNo = 0L or r.rackNo = :rackNo) " + //rack 번호 - 1~10 입력
            "AND (:searchName = '' or c.product = :searchName or c.componentState = :searchName ) ") //제품명 입력 or 공정상태 1~4 입력
    List<IOBounds> getSearchList(@Param("componentNo") String componentNo
            ,@Param("rackNo") long rackNo
            ,@Param("searchName") String searchName);

    @Query("SELECT io.inboundDate AS inboundDate" +
            ", io.componentNo AS componentNo" +
            ", c.product AS product" +
            ", io.componentAmount AS componentAmount" +
            ", c.componentState AS componentState" +
            ", r.rackId AS rackId " +
            "FROM IOBound io " +
            "JOIN Components c ON c.componentNo = io.componentNo " +
            "JOIN RackDetailInfo r ON r.rackDetailNo = io.rackDetailNo " )
    List<IOBounds> getAllSearchList();

    //iobound에 존재하는 componentNo 개수
    @Query("SELECT COUNT(i) FROM IOBound i WHERE i.componentNo LIKE CONCAT('%', :componentNo, '%')" )
    long countByComponentNoContainingParam(@Param("componentNo") String componentNo);

    //소재 무게 구하기
    @Query(value = "SELECT sWeight FROM Components WHERE componentNo = :componentNo")
    double selectSWeight(@Param("componentNo") String componentNo);

    //완제품 무게 구하기
    @Query(value = "SELECT fWeight FROM Components WHERE componentNo = :componentNo")
    double selectFWeight(@Param("componentNo") String componentNo);

    //완제품 여부 구하기
    @Query(value = "SELECT componentState FROM Components WHERE componentNo = :componentNo")
    long selectFinCompo(@Param("componentNo") String componentNo);


    //입고 처리 쿼리
    @Modifying
    @Query(value = "INSERT INTO io_bound SELECT :inboundNo, category_no, component_amount, component_no, now(), :palletNo,(SELECT rack_detail_no FROM rack_detail_info WHERE rack_id = rack_location), total_weight FROM temp WHERE temp_pallet_no = :tempPalletNo", nativeQuery = true)
    void insertIoBound(@Param("inboundNo") String inboundNo,
                       @Param("palletNo") long palletNo,
                       @Param("tempPalletNo") long tempPalletNo);

    //palletNo 출력
    @Query(value = "SELECT MAX(RIGHT(i.pallet_no,4)) FROM io_bound i WHERE (select date_format(i.inbound_date, '%Y%m%d')) = (Select date_format(sysdate(), '%Y%m%d'))", nativeQuery = true)
    Integer selectPalletNo();

    //모델명 구하기
    @Query(value = "SELECT c.model FROM Components c WHERE c.componentNo like concat('%', :componentNo, '%') AND c.componentState =:state")
    String selectModelName(@Param("componentNo") String componentNo, @Param("state")String state);

    //품번 출력 쿼리
    @Query(value = "SELECT c.componentNo FROM Components c WHERE c.componentNo like concat('%', :componentNo, '%') AND c.componentState =:state ")
    String selectComponentNo(@Param("componentNo") String componentNo, @Param("state")String state);

    //카테고리 출력
    @Query(value = "SELECT c.categoryNo FROM Category c WHERE c.categoryName = (SELECT co.categoryName FROM Components co WHERE co.componentNo = :componentNo)")
    long selectCategoryNo(@Param("componentNo") String componentNo);

    //카테고리에 따른 비어있는 렉 분류
    @Query("SELECT r.rackId FROM RackDetailInfo r" +
            " WHERE r.categoryNo = :categoryNo" +
            " AND r.rackDetailNo NOT IN (SELECT io.rackDetailNo FROM IOBound io)" +
            " AND r.rackId NOT IN (SELECT t.rackLocation FROM Temp t WHERE t.rackLocation <> null)" +
            " ORDER BY r.rackNo, r.rowNo DESC, r.columnNo" )
    List<String> selectRackId(@Param("categoryNo") long categoryNo);

    //품번 실시간 검색
//    @Query("SELECT DISTINCT SUBSTRING(c.componentNo, 1, LOCATE('-', c.componentNo) - 1) " +
//            "FROM Components c " +
//            "WHERE c.componentNo like concat('%', :componentNo, '%') " +
//            "OR (LOCATE('-', c.componentNo) = 0 AND c.componentNo like concat('%', :componentNo, '%'))")
//    List<String> findSelectComponentNoList(@Param("componentNo") String componentNo);

    @Query("SELECT c.componentNo " +
            "FROM Components c " +
            "WHERE c.componentNo like concat('%', :componentNo, '%')")
    List<String> findSelectComponentNoList(@Param("componentNo") String componentNo);

    //품번 및 소재 품번 실시간 검색
//    @Query(value = "SELECT DISTINCT SUBSTRING(component_no, 1, LOCATE('-', component_no) - 1) FROM components " +
//            "WHERE component_no LIKE CONCAT('%', :component_no , '%') OR (LOCATE('-', component_no) = 0 AND component_no LIKE CONCAT('%', :component_no, '%')) " +
//            "UNION ALL " +
//            "SELECT DISTINCT s_no FROM components WHERE s_no LIKE CONCAT('%', :component_no, '%')", nativeQuery = true)
//    List<String> findDistinctValues(@Param("component_no") String component_no);
}
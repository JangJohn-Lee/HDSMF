package com.hdsmf.repository;

import com.hdsmf.entity.Temp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.validation.Validator;
import java.util.List;

@Repository
public interface TempRepository extends JpaRepository<Temp, Long> {

    @Modifying
    @Query(value = "INSERT INTO temp(temp_pallet_no, category_no, component_amount, component_no, total_weight, process, model_name) VALUES(:palletNo, :categoryNo, :amount, :componentNo, :totalWeight, :process, :modelName)", nativeQuery = true)
    void saveTemp(@Param("palletNo") long palletNo, @Param("categoryNo") long categoryNo, @Param("amount") long amount, @Param("componentNo") String componentNo, @Param("totalWeight") double totalWeight, @Param("process") String process, @Param("modelName") String modelName);
    @Query(value = "SELECT temp_pallet_no FROM temp WHERE category_no = :categoryNo AND rack_location is null ORDER BY total_weight", nativeQuery = true)
    List<String> selectPalletNo(@Param("categoryNo") long categoryNo);

    @Modifying
    @Query(value = "UPDATE temp t SET t.rack_location = :rackLocation WHERE t.temp_pallet_no = :palletNo", nativeQuery = true)
    void insertRackLocation(@Param("rackLocation") String rackLocation, @Param("palletNo") long palletNo);

    @Modifying
    @Query(value = "UPDATE temp t SET t.reason = :reason WHERE t.temp_pallet_no = :palletNo", nativeQuery = true)
    void insertFailReason(@Param("reason") String reason, @Param("palletNo") long palletNo);

    @Query(value = "SELECT temp_pallet_no FROM temp WHERE total_weight >= 1500 and category_no = :categoryNo and right(rack_location,1) <> 1 and rack_location <> '적재 불가'", nativeQuery = true)
    List<String> selectMaxWeight(@Param("categoryNo") long categoryNo);

    @Query(value = "SELECT r.rackId FROM RackDetailInfo r WHERE r.categoryNo = :categoryNo AND r.rowNo = 1 AND r.rackDetailNo NOT IN(SELECT i.rackDetailNo FROM IOBound i) AND r.rackId NOT IN(SELECT t.rackLocation FROM Temp t) ORDER BY r.rowNo desc, r.rackNo, r.columnNo")
    List<String> selectFirstFloor(@Param("categoryNo") long categoryNo);

    @Query(value = "SELECT rackLocation FROM Temp WHERE tempPalletNo = :palletNo")
    String selectRackLocation(@Param("palletNo") long palletNo);

    @Query(value = "SELECT reason FROM Temp WHERE tempPalletNo = :palletNo")
    String selectFailReason(@Param("palletNo") long palletNo);

    @Modifying
    @Query(value = "DELETE FROM Temp")
    void deleteTemp();

    //palletNo 출력
    @Query(value = "SELECT MAX(RIGHT(t.temp_pallet_no,4)) FROM temp t", nativeQuery = true)
    Integer selectTempPalletNo();

    //입고 삭제
    @Transactional
    @Modifying
    @Query("DELETE FROM Temp t WHERE t.tempPalletNo = :tempPalletNo")
    void deleteByComponentNo(@Param("tempPalletNo") long tempPalletNo);

    //입고 조회
    @Transactional
    @Query("SELECT c FROM Temp c")
    List<Temp> selectAll();


}
package com.hdsmf.service;

import com.hdsmf.dto.RackDetailSearchDto;
import com.hdsmf.entity.IOBound;
import com.hdsmf.entity.RackDetailInfo;
import com.hdsmf.repository.IOBoundRepository;
import com.hdsmf.repository.RackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class RackService {

    private final RackRepository rackRepository;

    private final IOBoundRepository ioBoundRepository;

    //Rack별 row의 개수
    public int selectRowNo(long rackNo){                                        //Rack별 row의 개수
        return rackRepository.selectRowNo(rackNo);
    }

    //Rack별 column의 개수
    public int selectColNo(long rackNo){                                        //Rack별 column의 개수
        return rackRepository.selectColNo(rackNo);
    }

    //Rack 총 개수
    public int selectRackNo(){                                                  //Rack 총 개수
        return rackRepository.selectRackNo();
    }

    //Rack에 존재하는 카테고리의 이름 목록
    public String findDistinctCategoryNamesByRackNo(Long rackNo) {
        String str =  "";
        for(String item : rackRepository.findDistinctCategoryNamesByRackNo(rackNo)){
            str+= item + " ";
        }
        return str;
    }


    //Rack 번호를 통해 해당 rackNo를 가진 모든 디테일 리스트에 담기
    public List<RackDetailSearchDto> findRackNo(Long data) {
        List<RackDetailSearchDto> dtoList = rackRepository.findByRackNoWithJoins(data);
        return dtoList;
    }

    public Integer countRemain(Long data){
        List<RackDetailSearchDto> rackDetailSearchDtos = rackRepository.findByRackNoWithJoins(data);
        Integer nonNullInboundDateCount = rackDetailSearchDtos.stream()
                .filter(dto -> dto.getInboundDate() != null)
                .collect(Collectors.toList())
                .size();
        return nonNullInboundDateCount;
    }

    public String viewColor(Long data){
        List<RackDetailSearchDto> rackDetailSearchDtos = rackRepository.findByRackNoWithJoins(data);
        Integer nonNullInboundDateCount = (int) rackDetailSearchDtos.stream()
                .filter(dto -> dto.getInboundDate() != null)
                .count();

        if (nonNullInboundDateCount <= (0.3 * rackDetailSearchDtos.size())) {
            return "green"; // 30% 이하인 경우 녹색
        } else if (nonNullInboundDateCount <= (0.6 * rackDetailSearchDtos.size())) {
            return "yellow"; // 60% 이하인 경우 노란색
        } else if (nonNullInboundDateCount <= (0.9 * rackDetailSearchDtos.size())) {
            return "orange"; // 90% 이하인 경우 주황색
        } else {
            return "#FF0000"; // 100% 이상인 경우 빨간색
        }
    }

    // 문자열에서 Rack정보 추출
    public String parseCellValue(String cellValue) {
        // 예제: "R01F03N01"에서 "R01", "F03", "N01" 추출
        String rackNo = cellValue.substring(0, 3);
        String column = cellValue.substring(7, 9);
        String floor = cellValue.substring(5, 6);

        String resultRackId = rackNo + "-" + column + "-" + floor;

        // 이후에 필요한 작업 수행
        return resultRackId;
    }

    // 선택된 입고품 옮기는 로직
    public String moveToRack(String toMoveRackId, String targetRackId) throws EntityNotFoundException, RuntimeException {
        try {
            // 옮길 RackId에 대한 조회
            RackDetailInfo toMoveRackInfo = rackRepository.findByRackId(toMoveRackId)
                    .orElseThrow(EntityNotFoundException::new);

            // IOBound 테이블에서 toMoveRackInfo의 RackDetailNo 값으로 매칭되는 IOBound 찾기
            IOBound toMoveIoBound = ioBoundRepository.findByRackDetailNo(toMoveRackInfo.getRackDetailNo())
                    .orElseThrow(() -> new EntityNotFoundException("옮기려는 물품이 없습니다."));

            // 옮겨 갈 RackId에 대한 조회
            RackDetailInfo targetRackInfo = rackRepository.findByRackId(targetRackId)
                    .orElseThrow(EntityNotFoundException::new);

            // targetIoBound 변수 선언 및 조회
            IOBound targetIoBound = ioBoundRepository.findByRackDetailNo(targetRackInfo.getRackDetailNo())
                    .orElse(null);

            // targetIoBound의 IObound 정보가 존재할 경우
            if (targetIoBound != null) {
                throw new RuntimeException("옮길 위치에 물품이 존재합니다.");
            }

            // 총 중량 1500kg 이상 입고품은 1단에 적재
            if (toMoveIoBound.getTotalWeight() > 1500 && targetRackInfo.getRowNo() > 1) {
                throw new RuntimeException("총 중량 1500kg 이상 입고품은 1단에 적재 해야 합니다.");
            }

            // 옮길 입고품의 RackDetailNo를 목표 RackId의 RackDetailNo로 업데이트
            toMoveIoBound.setRackDetailNo(targetRackInfo.getRackDetailNo());
            return "이동하였습니다.";
        } catch (EntityNotFoundException e1) {
            throw e1;
        }
    }

}

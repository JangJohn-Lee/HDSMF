package com.hdsmf.dto;

import com.hdsmf.entity.Category;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
@AllArgsConstructor  // 생성자 추가
@NoArgsConstructor
public class CategoryDto {

    private Long categoryNo;

    private String categoryName;

    private String categoryColor;

    private static ModelMapper modelMapper = new ModelMapper();

    public static CategoryDto of(Category category){
        return  modelMapper.map(category, CategoryDto.class);
    }
}

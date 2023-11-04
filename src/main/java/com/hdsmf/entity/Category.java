package com.hdsmf.entity;

import com.hdsmf.dto.CategoryDto;
import com.hdsmf.dto.RackInfoDto;
import lombok.*;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.*;

@Entity
@Table(name = "category")
@Data
@ToString
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryNo;

    private String categoryName;

    @Column(nullable = false)
    private String categoryColor;


    @Builder
    Category(String categoryName, String categoryColor){
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
    }

    public static Category createCategory(CategoryDto categoryDto){
        return Category.builder()
                .categoryName(categoryDto.getCategoryName())
                .categoryColor(categoryDto.getCategoryColor())
                .build();
    }
}

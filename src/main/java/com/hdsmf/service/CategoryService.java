package com.hdsmf.service;

import com.hdsmf.dto.CategoryDto;
import com.hdsmf.entity.Category;
import com.hdsmf.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private EntityManager entityManager;

    public CategoryService(CategoryRepository categoryRepository,  EntityManager entityManager){
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;
    }

    public List<CategoryDto> getCategoryList() {
        List<CategoryDto> categoryDtos =  new ArrayList<>();

        for(Category category : categoryRepository.findAll()){
            categoryDtos.add(CategoryDto.of(category));
        }
        return categoryDtos;
    }

    public void saveCategories(List<CategoryDto> categoryDtos) {
        List<Category> categories = new ArrayList<>();

        for (CategoryDto categoryDto : categoryDtos) {
            categories.add(Category.createCategory(categoryDto));
        }

        categoryRepository.saveAll(categories); // 새로운 데이터 저장
    }

    public void updateCategories(List<CategoryDto> categoryDtos) {
        List<Category> categoriesToUpdateOrInsert = new ArrayList<>();

        // 1. CategoryDto 목록에서 categoryNo만을 추출
        List<Long> categoryNoListFromDto = categoryDtos.stream()
                .map(CategoryDto::getCategoryNo)
                .collect(Collectors.toList());

        // 2. 데이터베이스에서 모든 카테고리 목록을 가져옴
        List<Category> allExistingCategories = categoryRepository.findAll();

        List<Category> categoriesToDelete = allExistingCategories.stream()
                .filter(category -> !categoryNoListFromDto.contains(category.getCategoryNo()))
                .collect(Collectors.toList());

        // 3. 존재하지 않는 카테고리는 삭제
        categoryRepository.deleteAll(categoriesToDelete);

        for (CategoryDto categoryDto : categoryDtos) {
            Category existingCategory = allExistingCategories.stream()
                    .filter(category -> category.getCategoryNo().equals(categoryDto.getCategoryNo()))
                    .findFirst()
                    .orElse(null);

            if (existingCategory != null) {
                // 존재하는 Category 업데이트 로직
                existingCategory.setCategoryName(categoryDto.getCategoryName());
                existingCategory.setCategoryColor(categoryDto.getCategoryColor());
                categoriesToUpdateOrInsert.add(existingCategory);
            } else {
                // 새로운 Category 추가 로직
                categoriesToUpdateOrInsert.add(Category.createCategory(categoryDto));
            }
        }

        // 4. 변경된 Category 목록 저장
        categoryRepository.saveAll(categoriesToUpdateOrInsert);
    }

}

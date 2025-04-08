package com.project03.repository.search;

import com.project03.domain.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaterialSearch {
    Page<Material> searchAll(String[] types, String keyword, Pageable pageable);
}

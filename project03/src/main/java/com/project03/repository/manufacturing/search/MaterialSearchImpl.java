package com.project03.repository.manufacturing.search;

import com.project03.domain.Material;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialSearchImpl implements MaterialSearch{
    private final EntityManager entityManager;

    @Override
    public Page<Material> searchAll(String[] types, String keyword, Pageable pageable){
        StringBuilder jpql = new StringBuilder("SELECT b FROM Material b WHERE b.materialId > 0");

        if ((types != null && types.length > 0) && keyword != null){
            jpql.append(" AND (");

            for (int i = 0; i < types.length; i++){
                String type = types[i];
                switch (type){
                    case "n" :
                        jpql.append("b.materialName LIKE :keyword");
                        break;
                    case "s" :
                        jpql.append("b.materialSize LIKE :keyword");
                        break;
                    case "d" :
                        jpql.append("b.materialDescription LIKE :keyword");
                        break;
                }
                if (i < types.length -1){
                    jpql.append(" OR ");
                }
            }
            jpql.append(")");
        }
        jpql.append(" ORDER BY materialId DESC");

        TypedQuery<Material> query = entityManager.createQuery(jpql.toString(), Material.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(
                jpql.toString().replace("SELECT b", "SELECT COUNT(b)"), Long.class
        );

        if ((types != null && types.length > 0) && keyword != null){
            query.setParameter("keyword", "%" + keyword + "%");
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Material> list = query.getResultList();
        long count = countQuery.getSingleResult();

        return new PageImpl<>(list, pageable, count);
    }
}

package com.pix.keys.strategy;

import com.pix.keys.dto.SearchPixKeyRequestDto;
import com.pix.keys.exception.DataNotFoundException;
import com.pix.keys.model.Account;
import com.pix.keys.model.PixKey;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Component
public class FindByParametersStrategy implements FinderByPixKeys {

    private final EntityManager entityManager;

    public FindByParametersStrategy(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<PixKey> findPixKeys(SearchPixKeyRequestDto requestDto) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<PixKey> criteriaQuery = criteriaBuilder.createQuery(PixKey.class);

        Root<PixKey> root = criteriaQuery.from(PixKey.class);

        Predicate[] predicatesArray = getPredicates(requestDto, criteriaBuilder, root);

        criteriaQuery.select(root).where(predicatesArray);

        List<PixKey> resultList = entityManager.createQuery(criteriaQuery).getResultList();

        validateIfDataWereFound(resultList);

        return resultList;
    }

    private Predicate[] getPredicates(SearchPixKeyRequestDto requestDto, CriteriaBuilder criteriaBuilder, Root<PixKey> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(requestDto.containsKeyType()) {
            predicates.add(criteriaBuilder.equal(root.get("type"), requestDto.getKeyType()));
        }

        if(requestDto.containsCreationDate()) {
            predicates.add(criteriaBuilder.equal(root.get("creationDate"), requestDto.getCreationDate()));
        }

        if(requestDto.containsInactivationDate()) {
            predicates.add(criteriaBuilder.equal(root.get("inactivationDate"), requestDto.getInactivationDate()));
        }

        if(requestDto.containsBranchNumber()) {
            Join<PixKey, Account> pixKeyAccountJoin = root.join("account");
            predicates.add(criteriaBuilder.equal(pixKeyAccountJoin.get("branchNumber"), requestDto.getBranchNumber()));
        }

        if(requestDto.containsAccountNumber()) {
            Join<PixKey, Account> pixKeyAccountJoin = root.join("account");
            predicates.add(criteriaBuilder.equal(pixKeyAccountJoin.get("accountNumber"), requestDto.getAccountNumber()));
        }

        if(requestDto.containsAccountHolderName()) {
            Join<PixKey, Account> pixKeyAccountJoin = root.join("account");
            predicates.add(criteriaBuilder.equal(pixKeyAccountJoin.get("accountHolderName"), requestDto.getAccountHolderName()));
        }

        Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
        return predicatesArray;
    }

    private void validateIfDataWereFound(List<PixKey> pixKeys) {
        if(pixKeys == null || pixKeys.isEmpty()) {
            throw new DataNotFoundException("Não foram encontrados dados para essa consulta");
        }
    }
}

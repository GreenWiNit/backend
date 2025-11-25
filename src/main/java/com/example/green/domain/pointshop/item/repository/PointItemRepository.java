package com.example.green.domain.pointshop.item.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;

public interface PointItemRepository extends JpaRepository<PointItem, Long> {

	boolean existsByItemCode(ItemCode itemCode);

	@Query("select p from PointItem p where p.id = :id")
	Optional<PointItem> findById(Long id);

	boolean existsByItemCodeAndIdNot(ItemCode code, Long id);

}

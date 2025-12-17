package com.example.green.domain.pointshop.admin.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.green.domain.pointshop.admin.dto.request.AdminCreatePointShopRequest;
import com.example.green.domain.pointshop.admin.dto.request.AdminUpdatePointShopRequest;
import com.example.green.domain.pointshop.admin.exception.PointShopAdminException;
import com.example.green.domain.pointshop.admin.exception.PointShopAdminExceptionMessage;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.vo.Category;
import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;
import com.example.green.domain.pointshop.item.repository.PointItemRepository;
import com.example.green.domain.pointshop.item.service.PointItemService;
import com.example.green.domain.pointshop.item.service.command.PointItemCreateCommand;
import com.example.green.domain.pointshop.item.service.command.PointItemUpdateCommand;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;
import com.example.green.domain.pointshop.product.service.PointProductService;
import com.example.green.domain.pointshop.product.service.command.PointProductCreateCommand;
import com.example.green.domain.pointshop.product.service.command.PointProductUpdateCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointShopAdminService {

	private final PointItemService pointItemService;
	private final PointProductService pointProductService;
	private final PointItemRepository pointItemRepository;
	private final PointProductRepository pointProductRepository;

	//생성
	public Long create(AdminCreatePointShopRequest request) {

		if (request.type() == PointShopType.ITEM) {
			PointItemCreateCommand command = new PointItemCreateCommand(
				new ItemCode(request.code()),
				new ItemBasicInfo(request.name(), request.description()),
				new ItemMedia(request.thumbnailUrl()),
				new ItemPrice(request.price()),
				Category.ITEM
			);
			return pointItemService.create(command);
		}

		if (request.type() == PointShopType.PRODUCT) {
			PointProductCreateCommand command = new PointProductCreateCommand(
				new Code(request.code()),
				new BasicInfo(request.name(), request.description()),
				new Media(request.thumbnailUrl()),
				new Price(request.price()),
				new Stock(request.stock())
			);
			return pointProductService.create(command);
		}

		throw new PointShopAdminException(PointShopAdminExceptionMessage.NOT_FOUND_CATEGORY);
	}

	//수정
	public void update(AdminUpdatePointShopRequest request, Long productId) {
		//item 먼저 찾기
		Optional<PointItem> pointItem = pointItemRepository.findById(productId);
		if (pointItem.isPresent()) {
			PointItemUpdateCommand command = new PointItemUpdateCommand(
				new ItemCode(request.code()),
				new ItemBasicInfo(request.name(), request.description()),
				new ItemMedia(request.thumbnailUrl()),
				new ItemPrice(request.price()),
				Category.ITEM
			);
			pointItemService.updatePointItem(command, productId);
			return;
		}
		//product 찾기
		Optional<PointProduct> pointProduct = pointProductRepository.findById(productId);
		if (pointProduct.isPresent()) {
			PointProductUpdateCommand command = new PointProductUpdateCommand(
				new Code(request.code()),
				new BasicInfo(request.name(), request.description()),
				new Media(request.thumbnailUrl()),
				new Price(request.price()),
				new Stock(request.stock()),
				Category.PRODUCT
			);
			pointProductService.update(command, productId);
			return;
		}
		throw new PointShopAdminException(PointShopAdminExceptionMessage.NOT_FOUND_PRODUCT);
	}

	//삭제
	public void delete(Long productId) {
		Optional<PointItem> pointItem = pointItemRepository.findById(productId);
		if (pointItem.isPresent()) {
			pointItemService.delete(productId);
			return;
		}
		Optional<PointProduct> pointProduct = pointProductRepository.findById(productId);
		if (pointProduct.isPresent()) {
			pointProductService.delete(productId);
			return;
		}
		throw new PointShopAdminException(PointShopAdminExceptionMessage.NOT_FOUND_PRODUCT);
	}
}

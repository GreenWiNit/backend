package com.example.green.domain.pointshop.product.service.command;

import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.Stock;

public record PointProductUpdateCommand(
	Code code,
	BasicInfo basicInfo,
	Media media,
	Price price,
	Stock stock
) {
}

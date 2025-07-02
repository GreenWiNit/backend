package com.example.green.domain.pointshop.service.command;

import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;

public record PointProductUpdateCommand(
	Code code,
	BasicInfo basicInfo,
	Media media,
	Price price,
	Stock stock
) {
}

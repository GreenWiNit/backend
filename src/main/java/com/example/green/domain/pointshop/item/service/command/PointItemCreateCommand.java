package com.example.green.domain.pointshop.item.service.command;

import com.example.green.domain.pointshop.item.entity.vo.ItemBasicInfo;
import com.example.green.domain.pointshop.item.entity.vo.ItemCode;
import com.example.green.domain.pointshop.item.entity.vo.ItemMedia;
import com.example.green.domain.pointshop.item.entity.vo.ItemPrice;

public record PointItemCreateCommand(
	ItemCode itemCode,
	ItemBasicInfo info,
	ItemMedia media,
	ItemPrice price
) {
}


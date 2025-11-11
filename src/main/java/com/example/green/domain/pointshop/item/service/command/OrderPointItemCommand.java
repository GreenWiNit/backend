package com.example.green.domain.pointshop.item.service.command;

import com.example.green.domain.pointshop.item.entity.vo.PointItemSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;

public record OrderPointItemCommand(
	MemberSnapshot memberSnapshot,
	PointItemSnapshot pointItemSnapshot,
	Long itemId
) {
}

package com.example.green.domain.dashboard.growth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantGrowthItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	private String itemImgUrl;

	@Column(nullable = false)
	private double positionX;

	@Column(nullable = false)
	private double positionY;

	@Column(nullable = false)
	private boolean applicability;

	//낙관적 락 적용
	@Version
	private Long version;

	private PlantGrowthItem(
		Long memberId,
		String itemName,
		String itemImgUrl,
		double positionX,
		double positionY,
		boolean applicability
	) {
		this.memberId = memberId;
		this.itemName = itemName;
		this.itemImgUrl = itemImgUrl;
		this.positionX = positionX;
		this.positionY = positionY;
		this.applicability = applicability;
	}

	public static PlantGrowthItem create(
		Long memberId,
		String itemName,
		String itemImgUrl
	) {
		return new PlantGrowthItem(memberId, itemName, itemImgUrl, 0, 0, false);
	}

	public void changePosition(double x, double y) {
		this.positionX = x;
		this.positionY = y;
	}

	public void apply() {
		this.applicability = !this.applicability;
	}

}

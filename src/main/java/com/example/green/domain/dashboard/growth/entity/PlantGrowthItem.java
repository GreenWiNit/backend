package com.example.green.domain.dashboard.growth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private String itemName;

	@Column(nullable = false)
	private String itemImgUrl;

	@Column(nullable = false)
	private double positionX;

	@Column(nullable = false)
	private double positionY;

	@Column(nullable = false)
	private boolean applicability;

	private PlantGrowthItem(
		String itemName,
		String itemImgUrl,
		double positionX,
		double positionY,
		boolean applicability
	) {
		this.itemName = itemName;
		this.itemImgUrl = itemImgUrl;
		this.positionX = positionX;
		this.positionY = positionY;
		this.applicability = applicability;
	}

	public static PlantGrowthItem create(
		String itemName,
		String itemImgUrl
	) {
		return new PlantGrowthItem(itemName, itemImgUrl, 0, 0, false);
	}

	public void changePosition(double x, double y) {
		this.positionX = x;
		this.positionY = y;
	}

	public void apply() {
		this.applicability = true;
	}
}

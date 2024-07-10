package com.rido.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "find_item")
public class FindItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long itemId;
	private String itemDes;
	private Date dateOfRide;
	private String driverName;
	private String carNumber;
	
	
	public FindItem() {
		// TODO Auto-generated constructor stub
	}


	public FindItem(Long itemId, String itemDes, Date dateOfRide, String driverName, String carNumber) {
		super();
		this.itemId = itemId;
		this.itemDes = itemDes;
		this.dateOfRide = dateOfRide;
		this.driverName = driverName;
		this.carNumber = carNumber;
	}


	public Long getItemId() {
		return itemId;
	}


	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}


	public String getItemDes() {
		return itemDes;
	}


	public void setItemDes(String itemDes) {
		this.itemDes = itemDes;
	}


	public Date getDateOfRide() {
		return dateOfRide;
	}


	public void setDateOfRide(Date dateOfRide) {
		this.dateOfRide = dateOfRide;
	}


	public String getDriverName() {
		return driverName;
	}


	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}


	public String getCarNumber() {
		return carNumber;
	}


	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}


	@Override
	public String toString() {
		return "FindItem [itemId=" + itemId + ", itemDes=" + itemDes + ", dateOfRide=" + dateOfRide + ", driverName="
				+ driverName + ", carNumber=" + carNumber + "]";
	}
	
	
	
	

}

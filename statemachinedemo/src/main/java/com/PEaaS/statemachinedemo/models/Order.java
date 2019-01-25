package com.PEaaS.statemachinedemo.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.PEaaS.statemachinedemo.enums.OrderStates;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	@Id
	@GeneratedValue
	private Long id;
	private Date datetime;
	private String state;

	public Order() {
	}

	public Order(Date dt, OrderStates os) {
		this.datetime = dt;
		this.state = os.name();
	}

	public OrderStates getOrderState() {
		return OrderStates.valueOf(this.state);
	}

	public void setOrderState(OrderStates orderState) {
		this.state = orderState.name();
	}

	public Long getId() {
		return this.id;
	}
}

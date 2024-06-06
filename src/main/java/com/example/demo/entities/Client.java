package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Getter
@Setter
@ToString
@Table(name = "clients")
@Entity
@NoArgsConstructor
public class Client {
	public Client(String name, String email, String phone){
		this.name = name;
		this.email = email;
		this.phone = phone;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Integer id;

	@Column(length = 200, nullable = false)
	private String name;

	@Column(unique = true, length = 200, nullable = false)
	private String email;

	@Column(unique = true, length = 20, nullable = false)
	private String phone;

	@CreationTimestamp
	@Column(updatable = false, name = "created_at")
	private Date createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Date updatedAt;
}
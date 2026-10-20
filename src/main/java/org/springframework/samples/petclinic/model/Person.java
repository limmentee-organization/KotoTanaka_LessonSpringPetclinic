package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@MappedSuperclass		// JPAが提供するアノテーション。複数エンティティで共通するフィールドをまとめる親クラスに使用
public class Person extends BaseEntity {

	@Column(length = 30)
	@Size(max = 30)
	@NotBlank
	private String firstName;
	
	@Column(length = 30)
	@Size(max = 30)
	@NotBlank
	private String lastName;

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}

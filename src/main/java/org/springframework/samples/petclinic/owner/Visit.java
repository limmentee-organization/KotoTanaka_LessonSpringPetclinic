package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	@NotBlank
	private String description;
	
	/**
	 * 明日の「Visit」の新しいインスタンスを生成
	 */
	public Visit() {
		this.date = LocalDate.now().plusDays(1);
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
